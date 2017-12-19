package capture4;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
/**
 * @author javaQuery
 * Global Keyboard Listener
 */
public class JNativeHookCapture implements NativeKeyListener {
	
	private SampleTask2 sampletask2 = null;
	private boolean record = false;
	//private RecordAudio recordAudio = null;
	private Webcamtoencode4 webcamtoencode4; 
	private boolean first = false;
	
	public JNativeHookCapture(){
		this.sampletask2 = new SampleTask2();
		System.out.printf("%s\n",sampletask2);
	}
	
    /* Key Pressed */
    public void nativeKeyPressed(NativeKeyEvent e) {
        System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));

        /* Terminate program when one press ESCAPE */
        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
        	
        	if(this.record){
	        	//RecordAudio recordAudio = this.recordAudio;
        		Webcamtoencode4 webcamtoencode4 = this.webcamtoencode4;
	        	try {
	        		webcamtoencode4.recordStop();
					//recordAudio.stopRecord();
				} catch (Exception e2) {
					
					e2.printStackTrace();
				}
        	}
        	this.sampletask2.run(); 
        	try {
				this.sampletask2.fileClose();
			} catch (IOException e2) {
				
				e2.printStackTrace();
			}
            try {
				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException e1) {
				
				e1.printStackTrace();
			}
            System.out.println("complete.") ;
			System.exit(77) ;
        }
        
        if (e.getKeyCode() == NativeKeyEvent.VC_ENTER || e.getKeyCode() == NativeKeyEvent.VC_UP || e.getKeyCode() == NativeKeyEvent.VC_DOWN) {
        	 if(!this.first){
        		 
        		//RecordAudio recordAudio = new RecordAudio();
             	//this.recordAudio = recordAudio;
        		 try {
					Webcamtoencode4  webcamtoencode4 = new Webcamtoencode4();
					this.webcamtoencode4 = webcamtoencode4;
				} catch (LineUnavailableException e2) {
					// TODO 自動生成された catch ブロック
					e2.printStackTrace();
				}
        		 
             	if(this.record){
             		try {
     					//recordAudio.stopRecord();
             			webcamtoencode4.recordStop();
     					System.out.println("停止");
     				} catch (Exception e1) {
     					
     					e1.printStackTrace();
     				}
             	}
             	try {
             		System.out.printf("record\n");
     				//recordAudio.startRecord();
             		webcamtoencode4.recordStart();
     				this.record = true;
     			} catch (Exception e1) {
     				
     				e1.printStackTrace();
     			}
        		  this.first = true;
        		  this.sampletask2.trun();
        	 }
             this.sampletask2.run(); 
        }
    }

    /* Key Released */
    public void nativeKeyReleased(NativeKeyEvent e) {
        System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    /* I can't find any output from this call */
    public void nativeKeyTyped(NativeKeyEvent e) {
        System.out.println("Key Typed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }
    
    public SampleTask2 getSampleTask2(){
    	return this.sampletask2;
    }
    

    public static void main(String[] args) {
        try {
            /* Register jNativeHook */
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            /* Its error */
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        /* Construct the example object and initialze native hook. */
        GlobalScreen.addNativeKeyListener(new JNativeHookCapture());
    }
}
