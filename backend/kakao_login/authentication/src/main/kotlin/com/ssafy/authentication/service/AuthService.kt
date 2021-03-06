package com.ssafy.authentication.service

import com.ssafy.authentication.repository.AuthRepository
import com.ssafy.authentication.utils.JwtUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AuthService(private val authRepository: AuthRepository) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun publishAccessToken(token: String, type: String): Map<String, Any> {

        // get user info with access token
        val kakaoUserId = authRepository.getKakaoUserIdWithToken(token)
        logger.debug("Kakao ID Value : $kakaoUserId")
        // create hashed id with salt to prevent overwriting google id : SALT = KAKAO
        val hashedId = "${kakaoUserId}KAKAO"

        // create jwt token with hashed userid
        val jwtAccessToken =
            JwtUtil.createJwtAccessToken(hashedId) // token includes userid

        // check if id exists in DB, if it doesn't create new user
        // if user is already exists, use it's existing refresh token else create new one
        val jwtRefreshToken = authRepository.checkUserAndGetRefreshToken(hashedId, jwtAccessToken, type)

        // put tokens to result map
        val result: MutableMap<String, Any> = HashMap()
        result["access_token"] = jwtAccessToken
        result["refresh_token"] = jwtRefreshToken

        return result
    }

    fun publishAccessTokenForGoogle(userId: String): Map<String, Any> {
        val jwtAccessToken = JwtUtil.createJwtAccessToken(userId)
        val jwtRefreshToken = authRepository.checkUserAndGetRefreshToken(userId, jwtAccessToken)
        val result: MutableMap<String, Any> = HashMap()
        result["access_token"] = jwtAccessToken
        result["refresh_token"] = jwtRefreshToken
        return result
    }

    fun republishAccessToken(accessToken: String, refreshToken: String): String {
        // token format : Bearer alkdjaskldsajkldjaslkdjaslkdj
        println(accessToken)
        val userId = JwtUtil.decodeToken(accessToken)
        if (authRepository.validateRefreshToken(refreshToken))
            return JwtUtil.createJwtAccessToken(userId)
        throw RuntimeException("Something goes wrong...")
    }

}