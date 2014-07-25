package com.masseranolabs.snappyfrog;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.bindings.admob.GADAdSizeManager;
import org.robovm.bindings.admob.GADBannerView;
import org.robovm.bindings.admob.GADBannerViewDelegateAdapter;
import org.robovm.bindings.admob.GADRequest;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogic.gdx.utils.Logger;
import com.masseranolabs.bridge.SLComposeViewController;

public class RobovmLauncher extends IOSApplication.Delegate {
	// Admob stuff
	private static final Logger log = new Logger(RobovmLauncher.class.getName(), Application.LOG_DEBUG);
    private static final boolean USE_TEST_DEVICES = false;
    private GADBannerView adview;
    private boolean adsInitialized = false;
    private IOSApplication iosApplication;
    
    public static boolean isSharingAvailable(){ 
    	return SLComposeViewController.isAvailable(SLComposeViewController.ServiceTypeTwitter) ||
    		   SLComposeViewController.isAvailable(SLComposeViewController.ServiceTypeFacebook);    	    			
    }
    
    public static SLComposeViewController getSLComposeViewController(){ 
    	if (SLComposeViewController.isAvailable(SLComposeViewController.ServiceTypeTwitter)){
    		return SLComposeViewController.fromService(SLComposeViewController.ServiceTypeTwitter);
    	}else if (SLComposeViewController.isAvailable(SLComposeViewController.ServiceTypeFacebook)){
    		return SLComposeViewController.fromService(SLComposeViewController.ServiceTypeFacebook);
    	}
    	
    	return null;
    } 
    
    @Override
    protected IOSApplication createApplication() {
		final IOSApplicationConfiguration config = new IOSApplicationConfiguration();
		config.orientationLandscape = true;
		config.orientationPortrait = false;
		
		iosApplication = new IOSApplication(new Game(new iOSServices()), config);
        return iosApplication;
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, RobovmLauncher.class);
        pool.close();
    }
    
    @Override
    public boolean didFinishLaunching(UIApplication application,
    		NSDictionary<NSString, ?> launchOptions) {
    	super.didFinishLaunching(application, launchOptions);
    	showAds();
    	
    	return true;
    }
    
    

    public void showAds() {
        initializeAds();

        final CGSize screenSize = UIScreen.getMainScreen().getBounds().size();
        double screenWidth = screenSize.height();
        double screenHeight = screenSize.width();
        

        final CGSize adSize = adview.getBounds().size();
        double adWidth = adSize.width();
        double adHeight = adSize.height();

        log.debug(String.format("Hidding ad. size[%s, %s]", adWidth, adHeight));
        float bannerWidth = (float) screenWidth;
        float bannerHeight = (float) (bannerWidth / adWidth * adHeight);
        log.debug(String.format("%s, %s, %s", screenWidth, screenHeight, bannerHeight));

        adview.setFrame(new CGRect(0, 
        		screenHeight - bannerHeight, 
        		bannerWidth, bannerHeight));
    }

    public void initializeAds() {
        if (!adsInitialized) {
            log.debug("Initalizing ads...");

            adsInitialized = true;

            adview = new GADBannerView(GADAdSizeManager.smartBannerLandscape()); //.smartBannerPortrait()
            adview.setAdUnitID("ca-app-pub-3144450577280402/6483128178"); //put your secret key here
            adview.setRootViewController(iosApplication.getUIViewController());
            iosApplication.getUIViewController().getView().addSubview(adview);

            final GADRequest request = GADRequest.request();
            if (USE_TEST_DEVICES) {
                final NSArray<?> testDevices = new NSArray<NSObject>(
                        new NSString(GADRequest.GAD_SIMULATOR_ID));
                request.setTestDevices(testDevices);
                log.debug("Test devices: " + request.getTestDevices());
            }

            adview.setDelegate(new GADBannerViewDelegateAdapter() {
                @Override
                public void didReceiveAd(GADBannerView view) {
                    super.didReceiveAd(view);
                    //log.debug("didReceiveAd");
                }
            });

            adview.loadRequest(request);

            log.debug("Initalizing ads complete.");
        }
    }
}
