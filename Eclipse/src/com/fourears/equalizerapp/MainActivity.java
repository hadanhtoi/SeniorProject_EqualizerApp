package com.fourears.equalizerapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Equalizer equaliz;
	private LinearLayout mLinearLayout;
	private LinearLayout saves_page;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//mLinearLayout = new LinearLayout(this);
        //mLinearLayout.setOrientation(LinearLayout.VERTICAL);

        //setContentView(mLinearLayout);
		setContentView(R.layout.activity_main);
		//setupEqualizerAndUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void setupEqualizerAndUI(){
		//Create and attach a new Equalizer to the global audio stream
		equaliz = new Equalizer(0,0);
		equaliz.setEnabled(true);
		
		
		TextView eqTextView = new TextView(this);
        eqTextView.setText("Equalizer:");
        mLinearLayout.addView(eqTextView);

        short bands = equaliz.getNumberOfBands();      
        final short minEQLevel = equaliz.getBandLevelRange()[0];
        final short maxEQLevel = equaliz.getBandLevelRange()[1];
        
        for(short i=0; i<bands; i++){
        	final short band = i;
        	
        	//Setup text for frequency range
        	TextView freqTextView = new TextView(this);
            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            freqTextView.setText((equaliz.getCenterFreq(band) / 1000) + " Hz");
            mLinearLayout.addView(freqTextView);
            
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            
            //Setup text for min Db
            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            minDbTextView.setText((minEQLevel / 100) + " dB");
            
            //Setup text for max Db
            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            maxDbTextView.setText((maxEQLevel / 100) + " dB");
            
            //Setup seekbar
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            SeekBar bar = new SeekBar(this);
            bar.setLayoutParams(layoutParams);
            bar.setMax(maxEQLevel - minEQLevel);
            bar.setProgress(equaliz.getBandLevel(band));
            
            //Add listeners for each seekbar
            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress,
                        boolean fromUser) {
                    equaliz.setBandLevel(band, (short) (progress + minEQLevel));
                }

                public void onStartTrackingTouch(SeekBar seekBar) {}
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
            
            row.addView(minDbTextView);
            row.addView(bar);
            row.addView(maxDbTextView);

            mLinearLayout.addView(row);
        }
        /*Creates a Save button*/
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        Button save;
        
        save = new Button(this);
        save.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        save.setText("Save Configuration");
        //Setup button listener (checks for if clicked.)
        save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Equalizer.Settings toBeSaved = new Equalizer.Settings();
				toBeSaved = equaliz.getProperties();
				saveEqualizSetting(toBeSaved);	
			}
		});
        
        row.addView(save);
        
        mLinearLayout.addView(row);
        
        
	}

	public void openConfigPage(View v){
		mLinearLayout = new LinearLayout(this);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
		setContentView(mLinearLayout);
		setupEqualizerAndUI();
	}
	
	public void openSavePage(View v){
		setContentView(R.layout.saves);
		
		/*saves_page = new LinearLayout(this);*/
	}
	
	/* Grab a name from user and save it to the device */
	private void saveEqualizSetting(final Equalizer.Settings settings){
		
		AlertDialog.Builder savePrompt = new AlertDialog.Builder(this);
		savePrompt.setTitle("Custom Preset Name");
		savePrompt.setMessage("Enter a name for the new custom preset.");
		
		//Setup text field for user input
		final EditText input = new EditText(this);
		savePrompt.setView(input);
		
		//Setup save button on prompt
		savePrompt.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String FILENAME = input.getText().toString();
				try {
					FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(settings);
					
					//Close streams.
					fos.close();
					oos.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e){
					e.printStackTrace();
				}
			}//End onClick
		});
		
		//Do Nothing.
		savePrompt.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Cancelled	
			}
		});
		
		savePrompt.show();
	}

	
	public void openTutorialPage(View v){
		setContentView(R.layout.tutorial);
	}

}
