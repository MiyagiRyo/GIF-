import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.imageio.ImageIO;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;
import java.awt.DisplayMode;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import panel.Panel;
import input.Input;
import output.Output;

class Main extends JFrame{
  private int set_num,set_time;
  private JPanel menu1,menu2;
  private JButton select,play,make,save,zoom,reset;
  private File file;
  private Panel panel;
  private JSpinner page,time;
  private JComboBox<String> color,type;
  private boolean p_flag,z_flag,d_flag,f_flag;
  public int orgWidth,orgHeight;
  private int disWidth,disHeight;
  private int pre_w,pre_h;

  public Main(){
    orgWidth  = -1;
    orgHeight = -1;
    pre_w = -1;
    pre_h = -1;
    displaySizeInit();
    set_num   = -1;
    set_time  = -1;
    setSize(800,500);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setTitle("画像回転GIFメーカーver.1.4");
    setLayout(new BorderLayout());

    p_flag = false;
    z_flag = false;
    d_flag = true;
    f_flag = false;

    //メインパネル
    panel = new Panel();
    add(panel,BorderLayout.CENTER);

    //メニューパネル(上)
    menu1 = new JPanel();
    add(menu1,BorderLayout.NORTH);
    menu1.setLayout(new GridLayout(1,0));

    //回転種類選択コンボボックス
    String[] type_list = { "時計回り","反時計回り","X軸回転","Y軸回転","ドカベン" };
    type = new JComboBox<String>(type_list);
    menu1.add(type);

    //画像の表示倍率
    zoom = new JButton("100%表示");
    menu1.add(zoom);
    zoom.setEnabled(false);
    zoom.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent ae){
        z_flag = !z_flag;
        panel.setZoomFlag(z_flag);
        if( z_flag ){
          Main.this.zoom.setText("全体表示");
          if( f_flag || (orgWidth<disWidth && orgHeight<disHeight) || checkDisplay()!=d_flag ||
              checkDisplay()==d_flag && (pre_w!=getWidth() || pre_h!=getHeight() )){
            panel.setPreferredSize(new Dimension(orgWidth,orgHeight));
            setSize(orgWidth+40,orgHeight+80);
            f_flag = false;
            if( checkDisplay()!=d_flag ) d_flag = !d_flag;
            pre_w = getWidth();
            pre_h = getHeight();
          }
          panel.resetPoint();
          panel.repaint();
        }else{
          Main.this.zoom.setText("100%表示");
          panel.resetPoint();
          panel.repaint();
        }
      }
    });

    //画像の表示位置初期化
    reset = new JButton("位置初期化");
    menu1.add(reset);
    reset.setEnabled(false);
    reset.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        panel.resetPoint();
      }
    });

    //メニューパネル(下)
    menu2 = new JPanel();
    add(menu2,BorderLayout.SOUTH);
    menu2.setLayout(new GridLayout(1,0));

    //選択ボタン
    select = new JButton("選択");
    menu2.add(select);
    select.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent ae){
        Input input = new Input(Main.this);
        file = input.returnFile();
        if( file != null ){
          try{
            BufferedImage i = ImageIO.read(file);
            panel.setImage(i);
            orgWidth = i.getWidth();
            orgHeight = i.getHeight();
            pre_w = -1;
            pre_h = -1;
            f_flag = true;
            z_flag = false;
            setSize(-1,-1,false);
            make.setEnabled(true);
            zoom.setEnabled(true);
            zoom.setText("100%表示");
            reset.setEnabled(true);
            play.setEnabled(false);
            save.setEnabled(false);
          }catch(IOException ioe){}
        }
      }
    });

    //フレーム数の設定用スピナー
    page = new JSpinner(new SpinnerNumberModel(41,1,null,1));
    menu2.add(page);

    //時間用の設定用スピナー
    time = new JSpinner(new SpinnerNumberModel(2.0,0.1,null,0.1));
    menu2.add(time);

    //背景色の設定用コンボボックス
    String[] color_list = { "NONE","BLACK","WHITE","BLUE","RED","GREEN" };
    color = new JComboBox<String>(color_list);
    menu2.add(color);

    //作成ボタン
    make = new JButton("作成");
    menu2.add(make);
    make.setEnabled(false);
    make.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent ae){
        make.setText("作成中");
        make.setEnabled(false);
        set_num = (int)page.getValue();
        set_time = (int)((double)time.getValue()*1000/set_num);
        if( panel.create(set_num,set_time,(String)color.getSelectedItem(),type.getSelectedIndex()) ){
          make.setText("作成");
          make.setEnabled(true);
          play.setEnabled(true);
          save.setEnabled(true);
        }else{
          make.setText("失敗");
        }
      }
    });

    //再生ボタン
    play = new JButton("再生");
    menu2.add(play);
    play.setEnabled(false);
    play.addActionListener(new ActionListener(){
      boolean flag = false;
      public void actionPerformed(ActionEvent ae){
        if( !flag ){
          flag = true;
          play.setText("停止");
        }else{
          flag = false;
          play.setText("再生");
        }
        panel.play();
      }
    });

    //保存ボタン
    save = new JButton("保存");
    menu2.add(save);
    save.setEnabled(false);
    save.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent ae){
        Output output = new Output(Main.this,orgWidth,orgHeight,set_time/10,panel.returnImage());
      }
    });

    setVisible(true);
  }

  private void setSize(int w,int h,boolean flag){
    double as;
    if( !flag ){
      w = orgWidth;
      h = orgHeight;
    }
    if( w>disWidth && h>disHeight ){
      double asd = (double) disWidth / w;
      double ash = (double) disHeight/ h;
      as  = ( asd>ash ) ? ash : asd;
      setSize((int)(w*as),(int)(h*as));
    }else if( w>disWidth ){
      as = (double) disWidth / w;
      setSize((int)(w*as),(int)(h*as));
    }else if( h>disHeight ){
      as = (double) disHeight / h;
      setSize((int)(w*as),(int)(h*as));
    }else{
      setSize(w,h);
    }
  }

  //デフォルトディスプレイのサイズを記録
  private void displaySizeInit(){
    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    DisplayMode displayMode = env.getDefaultScreenDevice().getDisplayMode();
    disWidth = displayMode.getWidth();
    disHeight = displayMode.getHeight();
  }

  //デフォルトディスプレイにフレームがあるかどうかの確認
  private boolean checkDisplay(){
    int x = getX();
    int y = getY();
    if( x>disWidth || x+getWidth()<0 ) return false;
    else                               return true;
  }

  public static void main(String[] av){
    Main f = new Main();
  }
}
