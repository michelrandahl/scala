import javax.swing.ImageIcon
import java.net.URL
import scala.swing.event.ButtonClicked
import java.awt.image.BufferedImage
import java.io.FileOutputStream
import scala.swing._

trait FileHandling {
  def downloadImg(url: URL, fileName: String) = {
    val inStream = url.openStream()
    val outStream = new FileOutputStream(fileName)
    val b = new Array[Byte](2048)
    var length = inStream.read(b)
    do{
      outStream.write(b, 0, length)
      length = inStream.read(b)
    } while(length != -1)
    inStream.close()
    outStream.close()
  }
}

object ImageViewer extends SimpleSwingApplication with FileHandling {
  val MAXIMGNUMBER = 100 //no more images beyond 100
  val baseUrl = """http://www.nhm.ac.uk/resources/visit-us/whats-on/wpy/dev/2013/popup/"""
  var counter = 1
  val random = new Button { text = "random" }
  val save = new Button { text = "save" }
  val next = new Button { text = "next" }
  val previous = new Button { text = "previous" }

  def getImg = {
    val newImgIcon = new ImageIcon(new URL(baseUrl + counter + ".jpg"))
    val imgHeight = newImgIcon.getIconHeight
    if(imgHeight > 700) {
      val scale = 700.0 / imgHeight.toFloat
      val newHeight = (imgHeight * scale).toInt
      val newWidth = (newImgIcon.getIconWidth * scale).toInt
      val newImg = newImgIcon.getImage
      val bufImg = new BufferedImage(newImg.getWidth(null), newImg.getHeight(null), BufferedImage.TYPE_INT_ARGB)
      val graph = bufImg.createGraphics()
      graph.drawImage(newImg, 0, 0, newWidth, newHeight, null)
      new ImageIcon(bufImg)
    }
    else newImgIcon
  }

  val img = new Label { icon = getImg }

  listenTo(next, previous, save, random)
  reactions += {
    case ButtonClicked(`random`) =>
      counter = util.Random.nextInt(MAXIMGNUMBER) + 1
      println(counter)
      img.icon = getImg
    case ButtonClicked(`next`) =>
      counter += 1
      println(counter)
      img.icon = getImg
    case ButtonClicked(`previous`) =>
      if(counter > 1) {
        counter -= 1
        println(counter)
        img.icon = getImg
      }
    case ButtonClicked(`save`) =>
      val fileName = "awesomeImg_" + counter + ".jpg"
      val url = new URL(baseUrl + counter + ".jpg")
      downloadImg(url, fileName)
  }

  val ui = new BoxPanel(Orientation.Vertical) {
    contents += new FlowPanel(save, previous, next, random)
    contents += img
  }

  def top: Frame = new MainFrame {
    title = "awesome imgs"
    contents = ui
  }
}
