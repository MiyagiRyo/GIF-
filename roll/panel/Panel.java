package panel;

import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

import maker.Maker;

public class Panel extends JPanel implements Runnable,MouseListener,MouseMotionListener{
  private int main_flag;
  private int num,time;
  private int cx,cy;
  private int px,py;
  private int ox,oy;
  private boolean play_flag;
  private boolean gif_flag;
  private boolean zoom_flag;
  private BufferedImage readImage;
  private BufferedImage[] createImage;

  public Panel(){
    super();
    readImage = null;
    createImage = null;
    main_flag = -1;
    num = -1;
    time = -1;
    play_flag = false;
    gif_flag = false;
    zoom_flag = false;
    cx = -1; cy = -1;
    px = -1; py = -1;
    ox = -1; oy = -1;
    addMouseListener(this);
    addMouseMotionListener(this);
  }

  public void setImage(BufferedImage i){
    readImage = i;
    main_flag = 0;
    num = -1;
    play_flag = false;
    gif_flag = false;
    cx = 0;
    cy = 0;
    repaint();
  }

  public void paint(Graphics g){
    int tmp_w,tmp_h;

    g.fillRect(0,0,getWidth(),getHeight());

    tmp_w = getWidth();
    tmp_h = getHeight();
    switch( main_flag ){
      case 0:
        if( zoom_flag ) g.drawImage(readImage,cx,cy,this);
        else            g.drawImage(readImage,cx,cy,tmp_w,tmp_h,this);
        break;
      case 1:
        if( zoom_flag ) g.drawImage(createImage[num],cx,cy,this);
        else            g.drawImage(createImage[num],cx,cy,tmp_w,tmp_h,this);
        break;
      default:
    }
  }

  //再生, 停止
  public void play(){
    if( gif_flag && main_flag!=-1 ){
      if( !play_flag ){
        main_flag = 1;
        play_flag = true;
        if( num==-1 ) num = 0;
        new Thread(this).start();
      }else play_flag = false;
    }
  }

  //更新
  public void run(){
    while(play_flag){
      repaint();
      num = (num+1)%createImage.length;
      try{
        Thread.sleep(time);
      }catch(InterruptedException e){}
    }
  }

  //作成
  public boolean create(int n,int t,String c,int type){
    try{
      time = t;
      Maker thread = new Maker(n,readImage,createImage,c,type);
      thread.start();
      thread.join();
      createImage = thread.returnImage();
      if( createImage == null ) return false;
      else{
        main_flag = 1;
        gif_flag = true;
        num = 0;
        repaint();
        return true;
      }
    }catch(InterruptedException e){
      return false;
    }
  }

  public void setZoomFlag(boolean f){
    zoom_flag = f;
  }

  public void resetPoint(){
    if( !zoom_flag ){
      cx = 0; cy = 0;
    }else{
      int diff_w = getWidth()  - ( gif_flag ? createImage[0].getWidth()  : readImage.getWidth() );
      int diff_h = getHeight() - ( gif_flag ? createImage[0].getHeight() : readImage.getHeight() );
      cx = diff_w>0 ? diff_w/2 : 0;
      cy = diff_h>0 ? diff_h/2 : 0;
    }
    repaint();
  }

  public BufferedImage[] returnImage(){
    return createImage;
  }

  public void mouseEntered(MouseEvent e){}
  public void mouseExited(MouseEvent e){}
  public void mousePressed(MouseEvent e){
    px = e.getPoint().x;
    py = e.getPoint().y;
    ox = cx; oy = cy;
  }
  public void mouseReleased(MouseEvent e){}
  public void mouseClicked(MouseEvent e){}

  public void mouseDragged(MouseEvent e){
    int dx = e.getPoint().x;
    int dy = e.getPoint().y;
    cx = ox + 3*(dx - px);
    cy = oy + 3*(dy - py);
    repaint();
  }
  public void mouseMoved(MouseEvent e){}
}
