package com.fsck.k9.notification;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;

import com.fsck.k9.Account;
import com.fsck.k9.MockHelper;
import com.fsck.k9.core.R;
import com.fsck.k9.RobolectricTest;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class CertificateErrorNotificationsTest extends RobolectricTest {
    private static final boolean INCOMING = true;
    private static final boolean OUTGOING = false;
    private static final int ACCOUNT_NUMBER = 1;
    private static final String ACCOUNT_NAME = "TestAccount";


    private Notification notification;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;
    private NotificationController controller;
    private Account account;
    private CertificateErrorNotifications certificateErrorNotifications;
    private PendingIntent contentIntent;


    @Before
    public void setUp() throws Exception {
        notification = createFakeNotification();
        notificationManager = createFakeNotificationManager();
        builder = createFakeNotificationBuilder(notification);
        controller = createFakeNotificationController(notificationManager, builder);
        account = createFakeAccount();
        contentIntent = createFakeContentIntent();

        certificateErrorNotifications = new TestCertificateErrorNotifications();
    }

    @Test
    public void testShowCertificateErrorNotificationForIncomingServer() throws Exception {
        int notificationId = NotificationIds.getCertificateErrorNotificationId(account, INCOMING);

        certificateErrorNotifications.showCertificateErrorNotification(account, INCOMING);

        verify(notificationManager).notify(notificationId, notification);
        assertCertificateErrorNotificationContents();
    }

    @Test
    public void testClearCertificateErrorNotificationsForIncomingServer() throws Exception {
        int notificationId = NotificationIds.getCertificateErrorNotificationId(account, INCOMING);

        certificateErrorNotifications.clearCertificateErrorNotifications(account, INCOMING);

        verify(notificationManager).cancel(notificationId);
    }

    @Test
    public void testShowCertificateErrorNotificationForOutgoingServer() throws Exception {
        int notificationId = NotificationIds.getCertificateErrorNotificationId(account, OUTGOING);

        certificateErrorNotifications.showCertificateErrorNotification(account, OUTGOING);

        verify(notificationManager).notify(notificationId, notification);
        assertCertificateErrorNotificationContents();
    }

    @Test
    public void testClearCertificateErrorNotificationsForOutgoingServer() throws Exception {
        int notificationId = NotificationIds.getCertificateErrorNotificationId(account, OUTGOING);

        certificateErrorNotifications.clearCertificateErrorNotifications(account, OUTGOING);

        verify(notificationManager).cancel(notificationId);
    }

    private void assertCertificateErrorNotificationContents() {
        verify(builder).setSmallIcon(R.drawable.notification_icon_new_mail);
        verify(builder).setTicker("Certificate error for " + ACCOUNT_NAME);
        verify(builder).setContentTitle("Certificate error for " + ACCOUNT_NAME);
        verify(builder).setContentText("Check your server settings");
        verify(builder).setContentIntent(contentIntent);
        verify(builder).setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
    }

    private Notification createFakeNotification() {
        return mock(Notification.class);
    }

    private NotificationManagerCompat createFakeNotificationManager() {
        return mock(NotificationManagerCompat.class);
    }

    private Builder createFakeNotificationBuilder(Notification notification) {
        Builder builder = MockHelper.mockBuilder(Builder.class);
        when(builder.build()).thenReturn(notification);
        return builder;
    }

    private NotificationController createFakeNotificationController(NotificationManagerCompat notificationManager,
            NotificationCompat.Builder builder) {
        NotificationController controller = mock(NotificationController.class);
        when(controller.getContext()).thenReturn(RuntimeEnvironment.application);
        when(controller.getNotificationManager()).thenReturn(notificationManager);
        when(controller.createNotificationBuilder()).thenReturn(builder);
        return controller;
    }

    private Account createFakeAccount() {
        Account account = mock(Account.class);
        when(account.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(account.getDescription()).thenReturn(ACCOUNT_NAME);

        return account;
    }

    private PendingIntent createFakeContentIntent() {
        return mock(PendingIntent.class);
    }


    class TestCertificateErrorNotifications extends CertificateErrorNotifications {
        public TestCertificateErrorNotifications() {
            super(controller, mock(NotificationActionCreator.class));
        }

        @Override
        PendingIntent createContentIntent(Context context, Account account, boolean incoming) {
            return contentIntent;
        }
    }
}
