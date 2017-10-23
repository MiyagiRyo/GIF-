package output;

import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.IIOImage;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;

public class Output extends JFileChooser{
  private int selected;
  private File file;
  private BufferedImage image0;

  public Output(JFrame frame,int width,int height,int time,BufferedImage[] image){
    super(new File(".").getAbsoluteFile().getParent());
    setDialogTitle("gifアニメーションファイルの保存");
    setFileFilter(new FileNameExtensionFilter("gifファイル","gif"));
    selected = showSaveDialog(frame);
    image0 = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
    saveFile(time,image);
  }

  private void saveFile(int time,BufferedImage[] image){
    if( selected == JFileChooser.APPROVE_OPTION ){
      try{
        String f_name = getSelectedFile().toString();
        String exp = f_name.substring(f_name.length()-4).equals(".gif") ? new String("") : new String(".gif");
        file = new File(getSelectedFile().getAbsolutePath()+exp);
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("gif");
        ImageWriter writer = it.hasNext() ? it.next() : null;
        ImageOutputStream stream = ImageIO.createImageOutputStream(file);
        if( writer==null ) return;
        writer.setOutput(stream);
        writer.prepareWriteSequence(null);
  
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        IIOMetadata metadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(image0),iwp);
        String metaFormat = metadata.getNativeMetadataFormatName();
        Node root = metadata.getAsTree(metaFormat);

        IIOMetadataNode aes = new IIOMetadataNode("ApplicationExtensions");
        IIOMetadataNode ae = new IIOMetadataNode("ApplicationExtension");
        ae.setAttribute("applicationID","NETSCAPE");
        ae.setAttribute("authenticationCode","2.0");
        byte[] uo = { 0x1,0x0,0x0 };
        ae.setUserObject(uo);
        aes.appendChild(ae);
        root.appendChild(aes);

        IIOMetadataNode gce = new IIOMetadataNode("GraphicControlExtension");
        gce.setAttribute("disposalMethod","restoreToBackgroundColor");
        gce.setAttribute("userInputFlag","FALSE");
        gce.setAttribute("transparentColorFlag","TRUE");
        gce.setAttribute("delayTime",String.valueOf(time));
        gce.setAttribute("transparentColorIndex","0");
        root.appendChild(gce);

        metadata.setFromTree(metaFormat,root);

        for(int i=0;i<image.length;i++)
          writer.writeToSequence(new IIOImage(image[i],null,metadata),null);

        writer.endWriteSequence();
        stream.close();
      }catch(IOException e){}
    }
  }
}
