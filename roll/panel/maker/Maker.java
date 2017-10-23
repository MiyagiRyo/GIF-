package maker;

import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

public class Maker extends Thread{
  private BufferedImage original;
  private BufferedImage[] result;
  private int w,h;
  private int c_r,c_g,c_b;
  private int n;
  private int type;

  public Maker(int n,BufferedImage o,BufferedImage[] r,String c,int t){
    w = o.getWidth();
    h = o.getHeight();
    original = o;
    this.n = n;
    type = t;

    result = new BufferedImage[n];
    for(int i=0;i<result.length;i++)
      result[i] = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);

    setBackgroundColor(c);
  }

  public void run(){
    switch( type ){
      case 0: rolling00(1);  break;
      case 1: rolling00(-1); break;
      case 2: rolling01(-1); break;
      case 3: rolling01(1);  break;
      case 4: rolling02();   break;
      default:
    }
  }

  //画像中心回転
  private void rolling00(int a){
    for(int i=0;i<n;i++){
      AffineTransform af = new AffineTransform();
      af.rotate(a*((double)i/n)*360*Math.PI/180,w/2,h/2);
      paint(original,af,i);
    }
  }

  //x,y軸回転
  private void rolling01(int f){
    for(int i=0;i<n;i++){
      double   a =  f== 1 ? Math.cos( Math.toRadians(360 * ((double)i/n)) )         : 1.0;
      double   b =  f==-1 ? Math.sin( Math.toRadians(360 * ((double)(i+n/4.0)/n)) ) : 1.0;
      double   c =  f== 1 ? w*(1.0-a)/2 : 0.0;
      double   d =  f==-1 ? h*(1.0-b)/2 : 0.0;
      //double[] = { m00 , m10 , m01 , m11 , m02 , m12 }
      double[] m = { a , 0 , 0 , b , c , d  };
      AffineTransform af = new AffineTransform(m);
      paint(original,af,i);
    }
  }

  //ドガベン
  private void rolling02(){
    if( c_r!=-1 ) background(0);
    for(int i=1;i<n;i++){
      double a = Math.sin( Math.toRadians(180 * ((double)i/n)) );
      double[] m = { 1 , 0 , 0 , a , 0 , h*(1-a) };
      AffineTransform af = new AffineTransform(m);
      paint(original,af,i);
    }
  }

  private void paint(BufferedImage o,AffineTransform af,int idx){
    AffineTransformOp afop = new AffineTransformOp(af,AffineTransformOp.TYPE_BICUBIC);
    afop.filter(o,result[idx]);
    if( c_r!=-1 ) background(idx);
  }

  private void background(int index){
    for(int x=0;x<w;x++) for(int y=0;y<h;y++)
      if( checkAlpha(result[index].getRGB(x,y)) ) result[index].setRGB(x,y,rgb(c_r,c_g,c_b));
  }

  private void setBackgroundColor(String c){
    switch( c ){
      case "BLACK" : c_r=0;   c_g=0;   c_b=0; break;
      case "RED"   : c_r=255; c_g=0;   c_b=0; break;
      case "GREEN" : c_r=0;   c_g=255; c_b=0; break;
      case "BLUE"  : c_r=0;   c_g=0;   c_b=255; break;
      case "WHITE" : c_r=255; c_g=255; c_b=255; break;
      default      : c_r=-1;  c_g=-1;  c_b=-1;
    }
  }

  private boolean checkAlpha(int color){
    if( ((color>>24) & 0xff)<=200 ) return true;
    else return false;
  }

  private int rgb(int r,int g,int b){
    return 0xff<<24 | r<<16 | g<<8 | b;
  }

  private int argb(int r,int g,int b,int a){
    return a<<24 | r<<16 | g<<8 | b;
  }

  public BufferedImage[] returnImage(){
    return result;
  }
}
