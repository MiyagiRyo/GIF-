import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFrame;

class Input extends JFileChooser{
  private int selected;
  private File file;

  public Input(JFrame frame){
    super(new File(".").getAbsoluteFile().getParent());
    setDialogTitle("画像ファイル選択");
    FileFilter filter = new FileNameExtensionFilter("pngまたはjpeg","png","PNG","jpg","JPG","jpeg","JPEG");
    addChoosableFileFilter(filter);
    setAcceptAllFileFilterUsed(false);

    selected = showOpenDialog(frame);
    if( !checkFile() ) file = null;
  }

  private boolean checkFile(){
    if( selected != JFileChooser.APPROVE_OPTION ) return false;
    file = getSelectedFile();
    if( file.exists() && file.isFile() && file.canRead() ) return true;
    else return false;
  }

  public File returnFile(){
    return file;
  }
}
