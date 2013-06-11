/**
 * Controller to display and override Special AE Forecast
 *
 * @author auzzaman
 * @since 180.store
 */
public with sharing class StoreBatchAdminController {

    public with sharing class StoreBatchAdminControllerException extends Exception {}

    public String notificationMinute;

    public String getNotificationMinute() {
        return this.notificationMinute;
    }

    public void setNotificationMinute(String notifMinute) {
        this.notificationMinute = notifMinute;
    }

    public String jobName;

    public String getJobName() {
        return this.jobName;
    }

    public void setJobName(String job) {
        this.jobName = job;
    }

    public PageReference startScheduledJob() {
        /* FIXME: Remove before prod! */
System.debug('Inside startScheduledJob() = ' + jobName);
        Integer minOfHour = 0;
        try {
            minOfHour = Integer.valueOf(this.notificationMinute);
        } catch (Exception e) {
            minOfHour = 0;
        }

        if (minOfHour < 0 || minOfHour > 59) {
            minOfHour = 0;
        }
        StoreScheduleWrapper.start(minOfHour);
        /* FIXME: Remove before prod! */
System.debug('Scheduled Store Batch Job for ' + minOfHour + '-th minute.');
        return null;
    }
        //runs the batch job now to process report records
    public PageReference startScheduledJobNow() {
        Integer  BATCH_SIZE = 400;
        Id batchProcessId = Database.executeBatch(new SpecialistForecastBatch(), BATCH_SIZE);

        /* FIXME: Remove before prod! */
System.debug('Scheduled Store Batch Job for ' + batchProcessId);
        return null;
    }

}
