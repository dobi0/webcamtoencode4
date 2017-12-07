package capture4;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Date;
import java.util.TimerTask;
import java.util.Calendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;

public class SampleTask2 extends TimerTask{
		private boolean judge = false;	//tキー?が押されたときにtrue
	    private String TtimeStamp;
	    private String TtimeStamp2;
	    private String filename = "0";
	    private long dayDiff;
	    private File file = null;
	    private FileWriter pw = null;
		@Override
		public void run() {
			if(judge){
				Robot robot = null;
				try {
					robot = new Robot();
				} catch (AWTException e) {
					
					e.printStackTrace();
				}
		        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		        BufferedImage image = robot.createScreenCapture(
		            new Rectangle(0, 0, screenSize.width, screenSize.height));
		        
		        String tmpTimeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
		        SimpleDateFormat captureTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		   
		        Date dateFrom = null;
		        Date dateTo = null;
		        
		        try {
		            dateFrom = captureTime.parse(tmpTimeStamp);
		            dateTo = captureTime.parse(this.TtimeStamp);
		        } catch (ParseException e) {
		            e.printStackTrace();
		        }
		        	long dateTimeTo = dateTo.getTime();
		        	long dateTimeFrom = dateFrom.getTime();
		        	
		        	
		        	long dayDiff = ( dateTimeFrom - dateTimeTo) / (1000 );
		        	this.dayDiff = dayDiff;
		        	
		        	Date dateFrom2 = null;
		        	Date dateTo2 = null;
			        
			        try {
			            dateFrom2 = captureTime.parse(tmpTimeStamp);
			            dateTo2 = captureTime.parse(this.TtimeStamp);
			        } catch (ParseException e) {
			            e.printStackTrace();
			        }
			        	long dateTimeTo2 = dateTo2.getTime();
			        	long dateTimeFrom2 = dateFrom2.getTime();
			        	
			        	
			        	long dayDiff2 = ( dateTimeFrom2 - dateTimeTo2) - dayDiff * 1000;	
		        	
		        	
			        String tmpBookName = this.TtimeStamp2 + "/" + dayDiff + "_" + dayDiff2 + ".png";
		        	this.filename = this.TtimeStamp2 + "/" + dayDiff + "_" + dayDiff2 + ".png";
		        	
		        	if(this.file == null){
		        		file = new File(this.TtimeStamp2 + "/data.json");
		        		try {
							pw = new FileWriter(file);
							System.out.println("aaa");
							pw.write("[\n");
							pw.write("  {\"time\":0 ,");
						} catch (IOException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						}
		        	}
		        	
		        	try {
						this.pw.write("\"name\": \""+dayDiff + "_" + dayDiff2 + ".png" + "\"},\n  {\"time\":"+ dayDiff + " ,");
					} catch (IOException e1) {
						// TODO 自動生成された catch ブロック
						e1.printStackTrace();
					}
		        	
			        try {
						ImageIO.write(image, "PNG", new File(tmpBookName));
					} catch (IOException e) {
						
						e.printStackTrace();
					}
			}
			
			
			
	    }
		
		
		public void trun() {
			String TtimeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
			String TtimeStamp2 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS").format(Calendar.getInstance().getTime());
			File file1 = new File(TtimeStamp2);
			if (file1.mkdir()) {
        	    //System.out.println("フォルダの作成に成功しました");
        	} else {
        	    System.out.println("フォルダの作成に失敗しました");
        	}
			this.TtimeStamp = TtimeStamp;
			this.TtimeStamp2 = TtimeStamp2;
			this.judge = true;
		}
		
		public void fileClose() throws IOException{
			if(pw!=null){
				pw.close();
				
				FileReader fr;
				try {
					fr = new FileReader(this.TtimeStamp2 + "/data.json");
					BufferedReader br = new BufferedReader(fr);
					String line = br.readLine();
					int lineCount = 1;
					while( line != null )
		            {
						// 1行読み込むに成功するたびに、行数のカウントを1増やす。
		                lineCount++;
		                
						// readLine メソッドを使ってもう1行読み込む。
		                line = br.readLine();
		            }
					String removeWord = "time\":"+ dayDiff + " ,";
					int time = 9999;
					String replaceWord = "time\":"+ time + " ,\"name\": \"\"}\n}";
					String readText = fileRead(file, lineCount-1);
					String replaceText = readText.replaceAll(removeWord, replaceWord);
					fileWrite( file, replaceText );
					System.out.printf("%s\n%s\n%s\n",replaceWord,removeWord,readText);
					System.out.printf("lineCount=%d\n",lineCount-1);
				} catch (FileNotFoundException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
				
			
		}
		public static int diffrenceDays(String strDate1,String strDate2) throws ParseException{
			Date date1 = DateFormat.getDateInstance().parse(strDate1);
			Date date2 = DateFormat.getDateInstance().parse(strDate2);
			return differenceDays(date1,date2);
		}
		
		 
		public static int differenceDays(Date date1,Date date2) {
		    long datetime1 = date1.getTime();
		    long datetime2 = date2.getTime();
		    long one_date_time = 1000 * 60 * 60 * 24;
		    long diffDays = (datetime1 - datetime2) / one_date_time;
		    return (int)diffDays; 
		}

		private static String fileRead(File _file, int _gyo) {

	        StringBuffer fileRead = new StringBuffer("");

	        try {

	            // FileReaderクラスをインスタンス化
	            FileReader fr = new FileReader( _file );

	            // BufferedReaderクラスをインスタンス化
	            BufferedReader br = new BufferedReader( fr );

	            String str = null;

	            int count = 1;

	            while ( ( str = br.readLine() ) != null ) {

	                if ( count != _gyo) {
	                    fileRead.append(str + "\r\n");
	                } else {
	                    System.out.println( str+"：を削除" );
	                }
	                count++;
	            }

	        } catch ( FileNotFoundException ex ) {
	             System.out.println( ex );
	        } catch ( IOException ex ) {
	             System.out.println( ex );
	        }
	        return fileRead.toString();
	    }
		
		 private static void fileWrite(File _file, String _text){

		        try {

		            // FileWriterクラスをインスタンス化
		            FileWriter filewriter = new FileWriter( _file );

		            filewriter.write( _text );
		            int time = 9999;
		            filewriter.write("  {\"time\":"+ time + " ,\"name\": \"\"}\n]");
		            // ファイルを閉じる
		            filewriter.close();

		        } catch ( IOException ex ) {
		             System.out.println( ex );
		        }
		 }
}

