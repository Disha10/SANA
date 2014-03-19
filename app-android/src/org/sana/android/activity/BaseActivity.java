package org.sana.android.activity;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import org.sana.R;
import org.sana.android.app.Locales;
import org.sana.android.app.State.Keys;
import org.sana.android.content.DispatchResponseReceiver;
import org.sana.android.content.Intents;
import org.sana.android.content.Uris;
import org.sana.android.fragment.AuthenticationDialogFragment.AuthenticationDialogListener;
import org.sana.android.util.Logf;
import org.sana.android.util.UriUtil;
/**
 * Base class that contains basic functionalities and behaviors that all
 * activities should do. 
 * @author Sana Dev Team
 */
public abstract class BaseActivity extends FragmentActivity implements AuthenticationDialogListener{
    
	public static final String TAG = BaseActivity.class.getSimpleName();

    // Dialog for prompting the user that a long operation is being performed.
    ProgressDialog mWaitDialog = null;
    
	/**
     * Finishes the calling activity and launches the activity contained in
     * <code>intent</code>
     * @param intent
     */
    void switchActivity(Intent intent) {
        startActivity(intent);
        finish();
    }
    
	// Session related 
    public static final String INSTANCE_KEY = "instanceKey";
    public static final String SESSION_KEY = "sessionKey";
    
    private final AtomicBoolean mWaiting = new AtomicBoolean(false);
    
    // instanceKey initialized to some random value for the instance;
	private String mInstanceKey = UUID.randomUUID().toString();
	// Authenticated session key default is null;
	private String mSessionKey = null;

    protected Uri mSubject = Uri.EMPTY;
    protected Uri mEncounter = Uri.EMPTY;
    protected Uri mProcedure = Uri.EMPTY;
    protected Uri mObserver = Uri.EMPTY;
    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
		    // Extract data included in the Intent
			Log.d(TAG, "context: " + context.getClass().getSimpleName() + ", intent: " + intent.toUri(Intent.URI_INTENT_SCHEME));
			String text = intent.hasExtra(DispatchResponseReceiver.KEY_RESPONSE_MESSAGE)? intent.getStringExtra(DispatchResponseReceiver.KEY_RESPONSE_MESSAGE): "Upload Result Received: " + intent.getDataString();
			makeText(text);
		  }
		};
		
	/**
	 * Returns the value of the instance key which is created when the object is
	 * instantiated.
	 * @return
	 */
    protected String getInstanceKey(){
    	return mInstanceKey;
    }
	
	/**
	 * Returns the value of the session key. Warning: any key returned must be 
	 * authenticated with the session service.
	 * @return
	 */
    protected String getSessionKey(){
    	return mSessionKey;
    }
    
    /**
     * Sets the value of the session key. Warning: this method does not make
     * any atempt to validate whether the session is authenticated.
     * @param sessionKey
     */
    protected void setSessionKey(String sessionKey){
    	mSessionKey = sessionKey;
    }
     
    /**
     * Writes the state fields for this component to a bundle.
     * Currently this writes the following from the Bundle
     * <ul>
     * 	<li>instance key</li>
     * 	<li>session key</li>
     * 	<li>current encounter</li>
     * 	<li>current subject</li>
     * 	<li>current observer</li>
     * 	<li>current procedure</li>
     * </ul>
     * @param outState
     */
    protected void onSaveAppState(Bundle outState){
    	outState.putString(Keys.INSTANCE_KEY, mInstanceKey);
    	outState.putString(Keys.SESSION_KEY, mSessionKey);
    	outState.putParcelable(Intents.EXTRA_ENCOUNTER, mEncounter);
    	outState.putParcelable(Intents.EXTRA_SUBJECT, mSubject);
    	outState.putParcelable(Intents.EXTRA_PROCEDURE, mProcedure);
    	outState.putParcelable(Intents.EXTRA_OBSERVER, mObserver);
    }
    
    /**
     * Writes the state fields for this component to an Intent as Extras.
     * Currently this writes the following from the Intent.
     * <ul>
     * 	<li>instance key</li>
     * 	<li>session key</li>
     * 	<li>current encounter</li>
     * 	<li>current subject</li>
     * 	<li>current observer</li>
     * 	<li>current procedure</li>
     * </ul>
     * @param outState
     */
    protected void onSaveAppState(Intent outState){
    	outState.putExtra(Keys.INSTANCE_KEY, mInstanceKey);
    	outState.putExtra(Keys.SESSION_KEY, mSessionKey);
    	outState.putExtra(Intents.EXTRA_ENCOUNTER, mEncounter);
    	outState.putExtra(Intents.EXTRA_SUBJECT, mSubject);
    	outState.putExtra(Intents.EXTRA_PROCEDURE, mProcedure);
    	outState.putExtra(Intents.EXTRA_OBSERVER, mObserver);
    }
    
    /**
     * Sets the state fields for this component from an Intent.
     * Currently this attempts to read the following extras from the
     * Intent.
     * <ul>
     * 	<li>instance key</li>
     * 	<li>session key</li>
     * 	<li>current encounter</li>
     * 	<li>current subject</li>
     * 	<li>current observer</li>
     * 	<li>current procedure</li>
     * </ul>
     * 
     * @param inState
     */
    protected void onUpdateAppState(Intent inState){
    	String k = inState.getStringExtra(Keys.INSTANCE_KEY);
    	if(k != null)
    		mInstanceKey = new String(k);

		k = inState.getStringExtra(Keys.SESSION_KEY);
    	if(k!=null)
    		mSessionKey = new String(k);
    	
    	Uri temp = inState.getParcelableExtra(Intents.EXTRA_ENCOUNTER);
    	if(temp != null)
    		mEncounter = UriUtil.copyInstance(temp);
    	
    	temp = inState.getParcelableExtra(Intents.EXTRA_SUBJECT);
    	if(temp != null)
    		mSubject = UriUtil.copyInstance(temp);
    	
    	temp = inState.getParcelableExtra(Intents.EXTRA_PROCEDURE);
    	if(temp != null)
    		mProcedure = UriUtil.copyInstance(temp);
    	
    	temp = inState.getParcelableExtra(Intents.EXTRA_OBSERVER);
    	if(temp != null)
    		mObserver = UriUtil.copyInstance(temp);
    }
    
    /**
     * Sets the state fields for this component from a bundle.
     * Currently this attempts to read the following from the Bundle
     * <ul>
     * 	<li>instance key</li>
     * 	<li>session key</li>
     * 	<li>current encounter</li>
     * 	<li>current subject</li>
     * 	<li>current observer</li>
     * 	<li>current procedure</li>
     * </ul>
     * 
     * @param inState
     */
    protected void onUpdateAppState(Bundle inState){
    	String k = inState.getString(Keys.INSTANCE_KEY);
    	if(k!=null)
    		mInstanceKey = new String(k);
    	k = inState.getString(Keys.SESSION_KEY);
    	if(k!=null)
    		mSessionKey = new String(k);
    	
    	Uri temp = inState.getParcelable(Intents.EXTRA_ENCOUNTER);
    	if(temp != null)
    		mEncounter = UriUtil.copyInstance(temp);
    	
    	temp = inState.getParcelable(Intents.EXTRA_SUBJECT);
    	if(temp != null)
    		mSubject = UriUtil.copyInstance(temp);
    	
    	temp = inState.getParcelable(Intents.EXTRA_PROCEDURE);
    	if(temp != null)
    		mProcedure = UriUtil.copyInstance(temp);
    	
    	temp = inState.getParcelable(Intents.EXTRA_OBSERVER);
    	if(temp != null)
    		mObserver = UriUtil.copyInstance(temp);
    }

    /**
     * Writes the app state fields as extras to an Intent. Activities
     * will still need to call setResult(RESULT_OK, data) as well as
     * write any other data they wish to the data Intent.
     * 
     * @param data
     */
    protected void setResultAppData(Intent data){
    	onSaveAppState(data);
    }
    
    public Bundle getState(){
    	Bundle state = new Bundle();
    	onSaveAppState(state);
    	return state;
    }
    
    /*
     * (non-Javadoc)
     * @see com.actionbarsherlock.app.SherlockActivity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
    	onSaveAppState(savedInstanceState);
    }
    
    /*
     * (non-Javadoc)
     * @see com.actionbarsherlock.app.SherlockActivity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
    	onUpdateAppState(savedInstanceState);
    }
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	Intent intent = getIntent();
    	// get the fields from the launch intent extras
    	if(intent != null)
    		onUpdateAppState(intent);
    	Locales.updateLocale(this, getString(R.string.force_locale));
    }
    
    /**
     * Displays a progress dialog fragment with the provided message.
     * @param message
     */
    void showProgressDialogFragment(String message) {
        if (mWaitDialog != null && mWaitDialog.isShowing()) {
            hideProgressDialogFragment();
        }
        // No need to create dialog if this is finishing
        if(isFinishing())
        	return;
        
        mWaitDialog = new ProgressDialog(this);
        mWaitDialog.setMessage(message);
        mWaitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mWaitDialog.show();
        mWaiting.set(true);
    }
    
    /**
     * Hides the progress dialog if it is shown.
     */
    void hideProgressDialogFragment() {
    	mWaiting.set(false);
        if (mWaitDialog == null) {
            return;
        }
        // dismiss if finishing
    	try{
    		if(isFinishing())
    			mWaitDialog.dismiss();
    		else
    			mWaitDialog.hide();
    		} catch (Exception e){
    			e.printStackTrace();
    	}
    }
    
    final void cancelProgressDialogFragment(){
    	mWaiting.set(false);
    	try{
    		if(mWaitDialog != null && mWaitDialog.isShowing())
    			mWaitDialog.dismiss();
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    }
    
    public void setData(Uri uri){
		int code = Uris.getTypeDescriptor(uri);
		setData(code, uri);
	}
	
	public void setData(int code, Uri uri){
		switch(code){
		case Uris.ENCOUNTER_DIR:
		case Uris.ENCOUNTER_ITEM:
		case Uris.ENCOUNTER_UUID:
			mEncounter = uri;
			break;
		case Uris.OBSERVER_DIR:
		case Uris.OBSERVER_ITEM:
		case Uris.OBSERVER_UUID:
			mObserver = uri;
			break;
		case Uris.PROCEDURE_DIR:
		case Uris.PROCEDURE_ITEM:
		case Uris.PROCEDURE_UUID:
			mProcedure = uri;
			break;
		case Uris.SUBJECT_DIR:
		case Uris.SUBJECT_ITEM:
		case Uris.SUBJECT_UUID:
			mSubject = uri;
			break;
		default:
			break;
		}
	}
	
	protected void onPause(){
		super.onPause();
		if(isFinishing())
			cancelProgressDialogFragment();
	}
	


	/* (non-Javadoc)
	 * @see org.sana.android.fragment.AuthenticationDialogFragment.AuthenticationDialogListener#onDialogPositiveClick(android.support.v4.app.DialogFragment)
	 */
	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.sana.android.fragment.AuthenticationDialogFragment.AuthenticationDialogListener#onDialogNegativeClick(android.support.v4.app.DialogFragment)
	 */
	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		throw new UnsupportedOperationException();
	}
	
    protected void dump(){
    	Logf.D(this.getClass().getSimpleName(),"dump()", String.format("{ 'encounter': '%s',"
    			+" 'observer': '%s', 'subject': '%s', 'procedure': '%s' }",
    			mEncounter, mObserver, mSubject, mProcedure));
    }
    
    protected void dump(String method){
    	Logf.D(this.getClass().getSimpleName(),method+".dump()", String.format("{ 'encounter': '%s',"
    			+" 'observer': '%s', 'subject': '%s', 'procedure': '%s' }",
    			mEncounter, mObserver, mSubject, mProcedure));
    	
    }
    
    protected final void makeText(String text){
		makeText(text, Toast.LENGTH_LONG);
	}
    
    protected final void makeText(String text, int duration){
    	Locales.updateLocale(this, getString(R.string.force_locale));
		Toast.makeText(this, text, duration).show();
	}
    
    protected final void makeText(int resId){
    	makeText(resId, Toast.LENGTH_SHORT);
	}
    
    protected final void makeText(int resId, int duration){
    	makeText(getString(resId), duration);
	}
    
}
