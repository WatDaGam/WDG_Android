package com.example.watdagam.utils

import android.content.Context
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.rx
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo

class KakaoLoginService {
    companion object{
        private const val TAG = "WDG_kakaoLoginService"
        private const val APP_KEY = "27c267b47e61135cd098eb3fc9270bc6"

        fun initializeSdk(context: Context) {
            KakaoSdk.init(context, APP_KEY)
        }
        fun login(
            context: Context,
            onSuccess: (String) -> Unit,
            onFailure: () -> Unit,
        ) {
            var disposables = CompositeDisposable()

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.rx.loginWithKakaoTalk(context)
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext { error ->
                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
//                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
//                            Log.i(TAG, "카카오톡 로그인창에서 뒤로가기")
//                            Single.error(error)
//                        } else {
                            // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                            Log.i(TAG, "연결된 카카오 계정 없음")
                            UserApiClient.rx.loginWithKakaoAccount(context)
//                        }
                    }.observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ token ->
                        Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                        onSuccess(token.accessToken)
                    }, { error ->
                        Log.e(TAG, "카카오톡으로 로그인 실패", error)
                        onFailure()
                    }).addTo(disposables)
            } else {
                UserApiClient.rx.loginWithKakaoAccount(context)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ token ->
                        Log.i(TAG, "카카오 계정으로 로그인 성공 ${token.accessToken}")
                        onSuccess(token.accessToken)
                    }, { error ->
                        Log.e(TAG, "카카오 계정으로 로그인 실패", error)
                        onFailure()
                    }).addTo(disposables)
            }}
    }
}