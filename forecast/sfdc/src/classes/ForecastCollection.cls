/**
 * Custom Collection Iterable for Specialist Forecast Batch Job
 *
 * @author auzzaman
 * @since 180.store
 */
global with sharing class ForecastCollection implements Iterable<AggregateResult> {

    global Iterator<AggregateResult> Iterator() {
        return new ForecastIterable();
    }

}
