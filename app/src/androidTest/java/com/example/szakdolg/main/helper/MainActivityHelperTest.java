package com.example.szakdolg.main.helper;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.szakdolg.contacts.helper.ContactsApiHelper;
import com.example.szakdolg.model.conversation.ConversationApiHelper;
import com.example.szakdolg.model.message.api.MessageApiHelper;
import com.example.szakdolg.model.user.model.User;
import com.example.szakdolg.util.RandomUtil;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class MainActivityHelperTest {
    @Mock
    MessageApiHelper mockMessageApiHelper;
    @Mock
    ContactsApiHelper mockContactsApiHelper;
    @Mock
    ConversationApiHelper mockConversationApiHelper;
    @Mock
    User mockCurrentUser;
    Context realContext;
    private String token = "testToken";
    private MainActivityHelper mainActivityHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        realContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Initialize MainActivityHelper (or your actual class under test)
        mainActivityHelper = new MainActivityHelper(realContext, RandomUtil.getRandomString(5), RandomUtil.getRandomString(5));
    }
/*
    @Test
    public void testStartCacheCheckingWhenCacheExpired() {
        try (MockedStatic<SharedPreferencesUtil> mockedSharedPreferencesUtil = mockStatic(SharedPreferencesUtil.class)) {
            // Mock expired cache
            mockedSharedPreferencesUtil.when(() ->
                            SharedPreferencesUtil.getLongPreference(Mockito.eq(realContext), Mockito.eq(SharedPreferencesConstants.CACHE_EXPIRE)))
                    .thenReturn(System.currentTimeMillis() - 1000);

            // Act
            mainActivityHelper.startCacheChecking(token, mockCurrentUser);

            // Verify that API helpers are called
            Mockito.verify(mockMessageApiHelper).checkCachedMessages(token, realContext, mockCurrentUser);
            Mockito.verify(mockContactsApiHelper).checkCachedContacts(token, realContext, mockCurrentUser);
            Mockito.verify(mockConversationApiHelper).checkCachedConversation(token, realContext, mockCurrentUser);
            Mockito.verify(mockConversationApiHelper).checkCachedConversationParticipant(token, realContext, mockCurrentUser);
        }
    }

    @Test
    public void testStartCacheCheckingWhenCacheNotExpired() {
        try (MockedStatic<SharedPreferencesUtil> mockedSharedPreferencesUtil = mockStatic(SharedPreferencesUtil.class)) {
            // Mock non-expired cache
            mockedSharedPreferencesUtil.when(() ->
                            SharedPreferencesUtil.getLongPreference(Mockito.eq(realContext), Mockito.eq(SharedPreferencesConstants.CACHE_EXPIRE)))
                    .thenReturn(System.currentTimeMillis() + 10000);  // Cache not expired

            // Act
            mainActivityHelper.startCacheChecking(token, mockCurrentUser);

            // Verify that no API helpers are called when cache is valid
            Mockito.verifyNoInteractions(mockMessageApiHelper);
            Mockito.verifyNoInteractions(mockContactsApiHelper);
            Mockito.verifyNoInteractions(mockConversationApiHelper);
        }
    }*/
}
