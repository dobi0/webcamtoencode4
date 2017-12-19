package capture4;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.Mixer.Info;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;



import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDevice;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;


//-------------------------------------------------------------------
// webcamtoencode4
//
// 任意のwebカメラからの映像とマイクからの音声を動画としてファイルに
// 保存します.
// webcam-capture と xuggle の API をインポートして使用しています.
//
// Any webcam's vision and any microphone's audio encode into a .flv file.
// The file has named by date & time informations. This program should
// import webcam-capture APIs and Xuggler APIs.
//
// @author whiteTank...(@ameblo), Aug. 1 2015
//-------------------------------------------------------------------

public class Webcamtoencode4 extends JFrame  {

	static JButton button1 ;
	static JButton button2 ;
	static JButton button3 ;
	static JButton button4 ;
	static int camera_ID = 0 ;
	static int speaker_ID = 0 ;
	static int microphone_ID = 0 ;
	static boolean recording = false ;
	static String button1Str = "カメラ1" ;
	static String button2Str = "待機中" ;
	static String button3Str = "スピーカー" ;
	static String button4Str = "マイク" ;
	static int cameraOK_Num = 0 ;
	static CardLayout layout ;
	static JFrame window = null ;
	static Webcam awebcam[] = new Webcam [10] ;
	static ArrayList apanel = new ArrayList() ;
	static Dimension size = null ;

	static boolean subThFreeze = false ;
	static ArrayList camOK = new ArrayList() ;
	static ArrayList AudioLine = new ArrayList() ;
	static ArrayList AudioIn = new ArrayList() ;
	static ArrayList AudioOut = new ArrayList() ;
	static ArrayList webcamPtr = new ArrayList() ;

	static File file = new File("output.flv") ;
	static Info[] lines = null ;
	static DataLine.Info outInfo = null ;
	static DataLine.Info inInfo = null ;

	static SourceDataLine[] outputLine = new SourceDataLine[10] ;
	static TargetDataLine[] inputLine = new TargetDataLine[10] ;
	static AudioFormat audioFormat = null ;
	static int videoStreamIndex = 0 ;
	static int audioStreamIndex = 1 ;
	static int audioStreamId = 0 ;
    static int channelCount = 1;
    static int sampleRate = 22050 ; // Hz
    static int sampleCount = 1000 ;
    static int sampleingBits = 8 ;
    static boolean fputfile = false ;
    static boolean fOKaudio = true ;
    static int count = 0;

	public Webcamtoencode4() throws LineUnavailableException {

	    String webcamname[] = new String [10] ;
	    int cameranum = 0 ;


		// webカメラのインスタンスを配列に格納するループ
		int c = 0;
		for (Webcam webcam : Webcam.getWebcams()) {
			size = WebcamResolution.VGA.getSize() ;
			webcam.setViewSize( size ) ;
			webcamname[c] = webcam.getName() ;
			System.out.println("cam: "+c+" = "+webcamname[c]+".") ;
			awebcam[c++] = webcam ;
		}
		cameranum = c ;  // 検出したwebカメラ数を保存
		String pstr = "capture" ;
		Pattern p = Pattern.compile( pstr, Pattern.CASE_INSENSITIVE ) ;
		for( int i = 0 ; i < cameranum ; i++ ){
	    	Matcher m = p.matcher(webcamname[i]) ;
	    	if (m.find()){
	    		camOK.add(false) ;
	    	}else{
	    		camOK.add(true) ;
	    		cameraOK_Num++ ;
	    	}
	    }
		// オーディオ用dataline情報取得
		lines = AudioSystem.getMixerInfo();

	    // 検出したオーディオ用dataline数ループ
	    boolean mixerOK = false ;
	    System.out.println("aaaaaaaaaaaaaaa");
	    for(int i = 0; i < lines.length; i++) {
	    System.out.println(lines[i]);
	    }
	    AudioOut.add(0) ;
	    AudioIn.add(0) ;
	    System.out.printf("AudioIN.size() = %s\n",AudioIn.size());
	    System.out.printf("AudioIOut.size() = %s\n",AudioOut.size());
	    //if(AudioOut.size() > 0 && AudioIn.size() > 0){
	    	fOKaudio = true ;
	    /*}else{
	    	fOKaudio = false ;
	    	System.out.println("Audio-dataline has not exist!") ;
	    }*/

	    // 音声フォーマット情報定義
		audioFormat = new AudioFormat( (float)sampleRate, sampleingBits, 1, true, true ) ;

		// 出力用datalineの設定(スピーカーがSourceDataLine)
	    outInfo = new DataLine.Info( SourceDataLine.class, audioFormat ) ;
	    for( int i = 0 ; i < AudioOut.size() ; i++ ){
	    	outputLine[i] = (SourceDataLine) AudioSystem.getMixer(lines[(Integer) AudioOut.get(i)]).getLine(outInfo) ;
	    	outputLine[i].open( audioFormat, outputLine[i].getBufferSize() ) ;
	    	outputLine[i].start() ;
	    }

	    // 入力用datalineの設定(マイクがTargetDataLine)
	    inInfo = new DataLine.Info(TargetDataLine.class, audioFormat ) ;
	    for( int i = 0 ; i < AudioIn.size() ; i++ ){
	    	inputLine[i] = (TargetDataLine)AudioSystem.getMixer(lines[(Integer) AudioIn.get(i)]).getLine(inInfo) ;
	    	inputLine[i].open(audioFormat, inputLine[i].getBufferSize()) ;
	    	inputLine[i].start() ;
	    }


	    // カメラ映像表示用の WebcamPanel を作って ArrayList に登録
	    boolean firstcam = false ;
	 	for( int j = 0 ; j < cameranum ; j++ ){
	 		//if((boolean) camOK.get(j)){
	 		  if((Boolean) camOK.get(j)){
	 			if(!firstcam){
	 				camera_ID = j ;
	 				firstcam = true ;
	 			}
	 			apanel.add( new WebcamPanel(awebcam[j]) ) ;
	 			webcamPtr.add(j) ;
	 		}
	 	}



	 	// 動画保存用スレッド起動
	 	RecVideo rv = new RecVideo() ;
	 	rv.start() ;


	}

	public void recordStop(){
		fputfile = true ;
		recording = false ;
	}

	public void recordStart(){
		recording = true ;
	}
	// ボタン1の処理
	static class BuListener1 implements ActionListener
	{
		public void actionPerformed( ActionEvent e ){
			window.getContentPane().remove( (Integer) apanel.get( camera_ID ) ) ;
			System.out.print("remove "+camera_ID+", ") ;
			camera_ID++ ;

			if( camera_ID >= cameraOK_Num ){
				camera_ID = 0 ;
			}
			button1.setText( "カメラ" + String.valueOf( camera_ID + 1 ) ) ;

			System.out.println("add "+camera_ID+".") ;
			window.add( (Component) apanel.get( camera_ID ), BorderLayout.CENTER  ) ;

		}
	}

	// ボタン2の処理
	static class BuListener2 implements ActionListener
	{
		public void actionPerformed( ActionEvent e ){
			if( recording ){
				fputfile = true ;
			    recording = false ;
				button2.setText( "待機中" ) ;
				//System.out.println("b2 press(1)") ;
			}else{
				recording = true ;
				button2.setText( "録画中" ) ;
				//System.out.println("b2 press(0)") ;
			}
		}
	}

	// ボタン3の処理
	static class BuListener3 implements ActionListener
	{
		public void actionPerformed( ActionEvent e ){
			if(fOKaudio){
				int n = AudioOut.size() ;
				if(++speaker_ID >= n){
					speaker_ID = 0 ;
				}
				button3.setText( button3Str+String.valueOf( speaker_ID + 1 ) ) ;
			}
		}
	}


	// ボタン4の処理
	static class BuListener4 implements ActionListener
	{
		public void actionPerformed( ActionEvent e ){
			if(fOKaudio){
				int n = AudioIn.size() ;
				if(++microphone_ID >= n){
					microphone_ID = 0 ;
				}
				button4.setText( button4Str+String.valueOf( microphone_ID + 1 ) ) ;
			}
		}
	}


	// 動画保存処理スレッド
	static class RecVideo extends Thread {
		public void run(){
			long start = 0l ;
			boolean pref = false ;
			IMediaWriter writer = null ;
			int numBytesRead = 0 ;
			//int frameS = 0 ;

			// 各初期値設定
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss") ;
			Calendar ca = Calendar.getInstance() ;
			String dateStr = sdf.format(ca.getTime()) ;
			String filestr = "output" + dateStr + ".flv" ;
			System.out.println("new file made: "+filestr+".") ;

			// 動画ファイル書き込み用writerを作成
			writer = ToolFactory.makeWriter(file.getName()) ;
			System.out.println("file made: "+file.getName()+".") ;

			// 映像用、音声用のストリーム設定
			writer.addVideoStream(videoStreamIndex, 0, size.width, size.height) ;
			System.out.printf("%s\n",fOKaudio);
			if(fOKaudio){
				writer.addAudioStream(audioStreamIndex, 0, channelCount, sampleRate) ;
			}

			// メインループ
		    for ( int k = 0 ; ; k++ ) {
		    	// メインウィンドウクローズ時の処理
		    	//System.out.printf("%d\n" , count++);


		    	//System.out.println("Capture frame " + k) ;
		    	byte [] audioBytes ;

		    	// byte型配列に生の音声データを読み込む

		    	if(fOKaudio){
		    		audioBytes = new byte[ inputLine[microphone_ID].getBufferSize() / 2 ] ; // best size?
		    		numBytesRead =  inputLine[microphone_ID].read(audioBytes, 0, audioBytes.length) ;

		    		// 読み込んだ音声データをモニター用のスピーカーに流す
		    	}else{
		    		audioBytes = new byte [100] ; // dummy allocation
		    		numBytesRead = 100 ;
		    	}

		    	if(recording){
		    		if(!pref){
		    			start = System.currentTimeMillis() ;
		    			k = 0 ;
		    		}

		    		// webカメラから画像を取得する
					//BufferedImage image = ConverterFactory.convertToType(awebcam[(int) webcamPtr.get(camera_ID)].getImage(), BufferedImage.TYPE_3BYTE_BGR) ;
		    		BufferedImage image = ConverterFactory.convertToType(awebcam[(Integer) webcamPtr.get(camera_ID)].getImage(), BufferedImage.TYPE_3BYTE_BGR) ;
					IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P) ;
		    		IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000) ;
					frame.setKeyFrame(k == 0) ;
					frame.setQuality(0) ;
					writer.encodeVideo( videoStreamIndex, frame ) ; // フレーム書き込み
					//System.out.printf("%s\n" ,frame);
					// byte型配列(unsigned)をshort型配列(signed)に直す
			        // * サンプリングビット数を16bitにするとエラーが出ますが、
			        //   xugglerのバグではないかと言われています.
					if(fOKaudio){
						int numSamplesRead = numBytesRead ;

						short[] audioSamples = new short[ numSamplesRead ] ;
						if (audioFormat.isBigEndian()) {   // bigエンディアンの時
							for ( int i = 0 ; i < numSamplesRead ; i++ ) {
								audioSamples[i] = (short)((audioBytes[i] << 8) | 0x00) ;
							}
						}
						else {      // littleエンディアンの時
							for ( int i = 0 ; i < numSamplesRead ; i++ ) {
								audioSamples[i] = (short)(0x00 | audioBytes[i]) ;
							}
						}
						writer.encodeAudio( audioStreamIndex, audioSamples ) ;  // 音声書き込み
					}
				}
				if(fputfile){
					System.out.println("呼び出されました。");
					writer.close() ; // ファイルクローズ
					File nfile = new File( filestr ) ;
					file.renameTo( nfile ) ;
					System.out.println("Video recorded in file: " + nfile.getAbsolutePath()) ;

					// 新しいファイルを作成し、初期値設定
					ca = Calendar.getInstance() ;
					dateStr = sdf.format(ca.getTime()) ;

					filestr = "output" + dateStr + ".flv" ;
					System.out.println("new file made: "+filestr+".") ;
					//ofile.deleteOnExit(); // VM終了時に削除されるようにする

					writer = ToolFactory.makeWriter(file.getName()) ;
					writer.addVideoStream(videoStreamIndex, 0, size.width, size.height) ;
					if(fOKaudio){
						writer.addAudioStream(audioStreamIndex, 0, channelCount, sampleRate) ;
					}

					fputfile = false ;

				}
				pref = recording ;

				try {
					Thread.sleep(10) ; // 100 ms スリープ(10FPSにしています) (Thread.sleep(100) ;)
				} catch (InterruptedException e) {
					e.printStackTrace() ;
				}
			}
		}
	}


}