package singlejartest;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dukascopy.api.system.ClientFactory;
import com.dukascopy.api.system.IClient;
import com.dukascopy.api.system.ISystemListener;


public class Test {

	private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);
    private static String jnlpUrl = "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";
    private static String userName = "DEMO2opdUM";
    private static String password = "opdUM";
	
	public static void main(String[] args) throws Exception{
		
		final IClient client = ClientFactory.getDefaultInstance();
		client.connect(jnlpUrl, userName, password);
		
		
		client.setSystemListener(new ISystemListener() {
		    private int lightReconnects = 3;

		    @Override
		    public void onStart(long processId) {
		        LOGGER.info("Strategy started: " + processId);
		    }

		    @Override
		    public void onStop(long processId) {
		        LOGGER.info("Strategy stopped: " + processId);
		        if (client.getStartedStrategies().size() == 0) {
		            System.exit(0);
		        }
		    }

		    @Override
		    public void onConnect() {
		        LOGGER.info("Connected");
		        lightReconnects = 3;
		    }

		    @Override
		    public void onDisconnect() {
		        LOGGER.warn("Disconnected");
		        if (lightReconnects > 0) {
		            LOGGER.error("TRY TO RECONNECT, reconnects left: " + lightReconnects);
		            client.reconnect();
		            --lightReconnects;
		        } else {
		            try {
		                //sleep for 10 seconds before attempting to reconnect
		                Thread.sleep(10000);
		            } catch (InterruptedException e) {
		                //ignore
		            }
		            try {
		                client.connect(jnlpUrl, userName, password);
		            } catch (Exception e) {
		                LOGGER.error(e.getMessage(), e);
		            }
		        }
		    }
		});

	}

}
