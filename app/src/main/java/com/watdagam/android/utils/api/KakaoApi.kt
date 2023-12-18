package com.watdagam.android.utils.api

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object KakaoApi {
    private const val APP_KEY = "27c267b47e61135cd098eb3fc9270bc6"

    fun init(context: Context) {
        KakaoSdk.init(context, APP_KEY)
    }

    suspend fun login(context: Context): OAuthToken {
        return if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            // 카카오톡이 설치되어 있다면
            // 카카오톡으로 로그인을 시도한다.
            try {
                loginWithKakaoTalk(context)
            } catch (error: Throwable) {
                // 사용자의 취소로 로그인을 실패한 경우
                // 그대로 로그인을 취소한다.
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) throw error
                // 이외의 경우로 로그인을 실패한 경우
                // 카카오 계정으로 로그인을 시도한다,
                loginWithKakaoAccount(context)
            }
        } else {
            // 카카오톡으로 로그인할 수 없는 경우
            // 바로 카카오 계정 로그인을 시도한다.
            loginWithKakaoAccount(context)
        }
    }

    suspend fun loginWithKakaoTalk(context: Context): OAuthToken {
        return suspendCoroutine { continuation ->
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else if (token != null) {
                    continuation.resume(token)
                } else {
                    continuation.resumeWithException(RuntimeException("Kakao access token을 받아오는데 실패했습니다."))
                }
            }
        }
    }

    suspend fun loginWithKakaoAccount(context: Context): OAuthToken {
        return suspendCoroutine { continuation ->
            UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else if (token != null) {
                    continuation.resume(token)
                } else {
                    continuation.resumeWithException(RuntimeException("Kakao access token을 받아오는데 실패했습니다."))
                }
            }
        }
    }
}