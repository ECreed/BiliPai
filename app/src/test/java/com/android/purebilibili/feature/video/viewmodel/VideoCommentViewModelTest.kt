package com.android.purebilibili.feature.video.viewmodel

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlinx.collections.immutable.persistentListOf
import com.android.purebilibili.data.model.response.ReplyItem

class VideoCommentViewModelTest {

    @Test
    fun commentFailurePreservesPageAndRepliesWithoutPretendingToReachEnd() {
        val before = CommentUiState(
            replies = persistentListOf(ReplyItem(rpid = 1L)),
            nextPage = 2,
            grpcNextOffset = "next-cursor",
            isRepliesLoading = true
        )
        val failed = before.withCommentLoadFailure("请求超时")
        assertFalse(failed.isRepliesEnd)
        assertFalse(failed.isRepliesLoading)
        assertFalse(failed.canLoadComments()) // 不在失败后无限自动重试。
        assertEquals(before.replies, failed.replies)
        assertEquals(2, failed.nextPage)
        assertEquals("next-cursor", failed.grpcNextOffset)
        assertTrue(failed.copy(repliesError = null).canLoadComments())
        assertFalse(failed.copy(repliesError = null, isRepliesLoading = true).canLoadComments())
    }

    @Test
    fun `comment result applies only to current video subject`() {
        val firstVideo = CommentSubjectKey(oid = 1001L)
        val secondVideo = CommentSubjectKey(oid = 2002L)

        assertTrue(
            shouldApplyCommentSubjectResult(
                expectedSubject = firstVideo,
                currentSubject = firstVideo
            )
        )
        assertFalse(
            shouldApplyCommentSubjectResult(
                expectedSubject = firstVideo,
                currentSubject = secondVideo
            )
        )
    }

    @Test
    fun `sub reply result is dropped after video or root changes`() {
        val firstVideo = CommentSubjectKey(oid = 1001L)
        val secondVideo = CommentSubjectKey(oid = 2002L)

        assertTrue(
            shouldApplySubReplyResult(
                expectedSubject = firstVideo,
                expectedRootId = 11L,
                currentSubject = firstVideo,
                activeRootId = 11L,
                conversationActive = false
            )
        )
        assertFalse(
            shouldApplySubReplyResult(
                expectedSubject = firstVideo,
                expectedRootId = 11L,
                currentSubject = secondVideo,
                activeRootId = 11L,
                conversationActive = false
            )
        )
        assertFalse(
            shouldApplySubReplyResult(
                expectedSubject = firstVideo,
                expectedRootId = 11L,
                currentSubject = firstVideo,
                activeRootId = 22L,
                conversationActive = false
            )
        )
        assertFalse(
            shouldApplySubReplyResult(
                expectedSubject = firstVideo,
                expectedRootId = 11L,
                currentSubject = firstVideo,
                activeRootId = 11L,
                conversationActive = true
            )
        )
    }

    @Test
    fun `conversation result is dropped after dialog changes`() {
        val subject = CommentSubjectKey(oid = 1001L)

        assertTrue(
            shouldApplyConversationReplyResult(
                expectedSubject = subject,
                expectedRootId = 11L,
                expectedDialogId = 33L,
                currentSubject = subject,
                activeRootId = 11L,
                activeDialogId = 33L
            )
        )
        assertFalse(
            shouldApplyConversationReplyResult(
                expectedSubject = subject,
                expectedRootId = 11L,
                expectedDialogId = 33L,
                currentSubject = subject,
                activeRootId = 11L,
                activeDialogId = 44L
            )
        )
    }

    @Test
    fun `sub reply loading uses rest pn paging and clears grpc offset`() {
        val viewModelSource = File("src/main/java/com/android/purebilibili/feature/video/viewmodel/VideoCommentViewModel.kt")
            .readText()
        val repositorySource = File("src/main/java/com/android/purebilibili/data/repository/CommentRepository.kt")
            .readText()

        assertFalse(viewModelSource.contains("loadSubReplies(requestSubject, rootReply.rpid, 1, paginationOffset = null)"))
        assertTrue(viewModelSource.contains("paginationOffset = paginationOffset"))
        assertTrue(viewModelSource.contains("grpcNextOffset = null"))
        assertTrue(repositorySource.contains("preferRestPaging: Boolean = true"))
    }
}
