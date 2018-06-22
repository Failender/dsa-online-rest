package de.failender.heldensoftware;

import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.requests.ApiRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class FailedRequestsRetrier {

	private static final int WORKER_SLEEP_TIME = 10000;

	private final HeldenApi heldenApi;
	private ConcurrentLinkedQueue<ApiRequest> failedRequests = new ConcurrentLinkedQueue<>();

	public FailedRequestsRetrier(HeldenApi heldenApi) {
		this.heldenApi = heldenApi;
		spawnWorker();
	}

	private void spawnWorker() {
		new Thread(() -> {
			while(true) {
				log.info("Starting to retry failed requests {}", failedRequests.size());
				Iterator<ApiRequest> apiRequestIterator = failedRequests.iterator();
				while(apiRequestIterator.hasNext()) {
					ApiRequest apiRequest = apiRequestIterator.next();
					//Always remove, since the api will re-add if the request fails
					apiRequestIterator.remove();
					try {
						log.info("Retriing request {} {}", apiRequest.url(), apiRequest.writeRequest());
						heldenApi.requestRaw(apiRequest, false).ifPresent((is) -> IOUtils.closeQuietly(is));
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				try {
					Thread.sleep(WORKER_SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}


			}
		}).start();
	}



	public void addFailedRequest(ApiRequest apiRequest) {
		failedRequests.add(apiRequest);
	}

}
