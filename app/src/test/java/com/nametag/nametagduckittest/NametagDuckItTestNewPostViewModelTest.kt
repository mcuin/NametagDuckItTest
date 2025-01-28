package com.nametag.nametagduckittest

import android.os.Build.VERSION_CODES.Q
import com.nametag.nametagduckittest.utils.EncryptionRepository
import com.nametag.nametagduckittest.utils.NametagDuckItTestNewPostRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@Config(sdk = [Q], application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class NametagDuckItTestNewPostViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Mock
    lateinit var mockNameTagDuckItTestNewPostRepository: NametagDuckItTestNewPostRepository
    lateinit var mockEncryptionRepository: EncryptionRepository
    private lateinit var nametagDuckItTestNewPostViewModel: NametagDuckItTestNewPostViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        hiltRule.inject()
        mockEncryptionRepository = Mockito.mock(EncryptionRepository::class.java)
        nametagDuckItTestNewPostViewModel = NametagDuckItTestNewPostViewModel(
            mockNameTagDuckItTestNewPostRepository,
            mockEncryptionRepository
        )
    }

    @Test
    fun testHeadlineStateUpdate() {
        var newPostUiState: NewPostUiState
        nametagDuckItTestNewPostViewModel.updateHeadlineText("Test")
        newPostUiState = nametagDuckItTestNewPostViewModel.newPostUIState.value
        assert(newPostUiState.headlineText == "Test")
    }

    @Test
    fun testHeadlineErrorStateUpdate() {
        var newPostUiState: NewPostUiState
        nametagDuckItTestNewPostViewModel.updateHeadlineText("")
        newPostUiState = nametagDuckItTestNewPostViewModel.newPostUIState.value
        assert(newPostUiState.headlineError)
    }

    @Test
    fun testImageLinkStateUpdate() {
        var newPostUiState: NewPostUiState
        nametagDuckItTestNewPostViewModel.updateImageUri("https://test.com")
        newPostUiState = nametagDuckItTestNewPostViewModel.newPostUIState.value
        assert(newPostUiState.imageLink == "https://test.com")
    }

    @Test
    fun testImageLinkErrorUpdate() {
        var newPostUiState: NewPostUiState
        nametagDuckItTestNewPostViewModel.updateImageUri("")
        newPostUiState = nametagDuckItTestNewPostViewModel.newPostUIState.value
        assert(newPostUiState.imageError)
    }

    @Test
    fun testSubmitReadyState() {
        var newPostUiState = nametagDuckItTestNewPostViewModel.newPostUIState.value.copy(successState = UploadSuccessState.Error)
        nametagDuckItTestNewPostViewModel.updateReadyState()
        newPostUiState = nametagDuckItTestNewPostViewModel.newPostUIState.value
        assert(newPostUiState.successState == UploadSuccessState.Ready)
    }
}