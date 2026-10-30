package com.pingo.alertacrypto.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.pingo.alertacrypto.data.local.entity.AlertEntity
import com.pingo.alertacrypto.data.local.entity.AlertNotificationEventEntity
import com.pingo.alertacrypto.data.local.entity.LatestPriceEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlertDatabaseTest {
    private lateinit var context: Context
    private lateinit var database: AppDatabase

    @Before
    fun createDatabase() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun persistsAlertPriceAndUniqueNotificationOutboxEvent() = runBlocking {
        val alert = alert()
        database.alertDao().insert(alert)
        assertEquals(alert, database.alertDao().getById(alert.id))

        database.alertDao().setEnabled(
            id = alert.id,
            enabled = false,
            lastPriceBRL = "1234.56",
            updatedAt = 200L,
        )
        val disabledAlert = database.alertDao().getById(alert.id)
        assertNotNull(disabledAlert)
        assertEquals(false, disabledAlert?.enabled)
        assertEquals("1234.56", disabledAlert?.lastPriceBRL)
        database.alertDao().deleteById(alert.id)
        assertEquals(null, database.alertDao().getById(alert.id))

        val price = LatestPriceEntity(
            symbol = "BTC",
            priceBRL = "1234.56",
            change24hPercent = "1.25",
            observedAtEpochMillis = 100L,
            receivedAtEpochMillis = 150L,
            sourceId = "binance_ws",
        )
        database.latestPriceDao().upsert(price)
        assertEquals(price, database.latestPriceDao().getBySymbol("BTC"))

        val event = notificationEvent()
        assertTrue(database.alertNotificationEventDao().insertIfAbsent(event) > 0L)
        val duplicate = database.alertNotificationEventDao().insertIfAbsent(
            event.copy(eventId = "second-event"),
        )
        assertEquals(-1L, duplicate)
        assertEquals(1, database.alertNotificationEventDao().getPending().size)
    }

    private fun alert() = AlertEntity(
        id = "alert-1",
        symbol = "BTC",
        targetPriceBRL = "1200",
        direction = "UP",
        repeatMode = "REPEATING",
        armed = true,
        firedUp = null,
        lastPriceBRL = null,
        enabled = true,
        createdAtEpochMillis = 100L,
        updatedAtEpochMillis = 100L,
        lastFiredAtEpochMillis = null,
        lastFiredTickAtEpochMillis = null,
    )

    private fun notificationEvent() = AlertNotificationEventEntity(
        eventId = "first-event",
        alertId = "alert-1",
        symbol = "BTC",
        directionUp = true,
        targetPriceBRL = "1200",
        observedPriceBRL = "1234.56",
        tickObservedAtEpochMillis = 150L,
        deliveryStatus = "PENDING",
        createdAtEpochMillis = 150L,
        updatedAtEpochMillis = 150L,
    )
}
