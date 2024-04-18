package com.hacom.replicatoragent.metric;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.Counter;

import java.util.HashMap;
import java.util.Map;

@Service
public class MetricService {

    public final static String METRICS_PREFIX = "hacom.pws.replicator.agent";

    private final Counter receivedMetric;
    private final Map<String, Counter> processSuccessfulMetric = new HashMap<>();
    private final Map<String, Counter> processErrorMetric = new HashMap<>();
    private final Map<String, Counter> sendMetric = new HashMap<>();
    private final Map<String, Counter> sendMetricError = new HashMap<>();
    private final Map<String, Counter> metrics = new HashMap<>();
    private final MeterRegistry meterRegistry;

    public MetricService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        receivedMetric = Counter.builder(METRICS_PREFIX + ".capReceivedOkay")
                .description("Messages received Okay Authentication.")
                .register(meterRegistry);
    }

    public void setReceivedMetric(){
        receivedMetric.increment();
    }

    public void setProcessSuccessfulMetric(String idAccount){
        if(processSuccessfulMetric.containsKey(idAccount)){
            processSuccessfulMetric.get(idAccount).increment();
        } else {
            processSuccessfulMetric.put(idAccount,
                    Counter.builder(METRICS_PREFIX + ".process.successful")
                            .description("The received message was processed successfully.")
                            .register(meterRegistry));
            processSuccessfulMetric.get(idAccount).increment();
        }
    }

    public void setProcessErrorMetric(String idAccount){
        if(processErrorMetric.containsKey(idAccount)){
            processErrorMetric.get(idAccount).increment();
        } else {
            processErrorMetric.put(idAccount,
                    Counter.builder(METRICS_PREFIX + ".process.error")
                            .description("The received message was not processed, it has errors.")
                            .register(meterRegistry));
            processErrorMetric.get(idAccount).increment();
        }
    }

    public void setSendMetric(String idAccount){
        if(sendMetric.containsKey(idAccount)){
            sendMetric.get(idAccount).increment();
        } else {
            sendMetric.put(idAccount,
                    Counter.builder(METRICS_PREFIX + ".send.successful")
                            .description("The processed message sent to the CBC.")
                            .register(meterRegistry));
            sendMetric.get(idAccount).increment();
        }
    }

    public void setSendMetricError(String idAccount){
        if(sendMetricError.containsKey(idAccount)){
            sendMetricError.get(idAccount).increment();
        } else {
            sendMetricError.put(idAccount,
                    Counter.builder(METRICS_PREFIX + ".send.error")
                            .description("The processed message sent to the CBC.")
                            .register(meterRegistry));
            sendMetricError.get(idAccount).increment();
        }
    }
    
    public void setMetric(String id, String description){
        String metricKey = METRICS_PREFIX + "." + id;
        if(metrics.containsKey(metricKey)){
            metrics.get(metricKey).increment();
        } else {
            Counter newCounter = Counter.builder(metricKey)
                    .description(description)
                    .register(meterRegistry);
            newCounter.increment();
            metrics.put(metricKey, newCounter);
        }
    }
    
}
