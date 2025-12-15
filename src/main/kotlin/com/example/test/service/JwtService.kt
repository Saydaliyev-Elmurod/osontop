package com.example.test.service


import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey


@Service
class JwtService(
  @Value($$"${application.token.signing.key}") private val jwtSigningKey: String
) {

  private val signingKey: SecretKey by lazy {
    val keyBytes = Decoders.BASE64.decode(jwtSigningKey)
    Keys.hmacShaKeyFor(keyBytes)
  }

  fun extractSubject(token: String): String = extractClaim(token, Claims::getSubject)

  fun getAllClaims(token: String): Claims = extractAllClaims(token)

  fun generateToken(userId: UUID, deviceId: UUID, sessionId: UUID, iss: String): String {
    val extraClaims = mapOf(
      "userId" to userId,
      "deviceId" to deviceId,
      "sessionId" to sessionId
    )
    return generateToken(extraClaims, userId.toString(), iss)
  }

  fun isTokenValid(token: String, sub: String): Boolean {
    val subject = extractSubject(token)
    return sub == subject && !isTokenExpired(token)
  }

  private fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
    val claims = extractAllClaims(token)
    return claimsResolver(claims)
  }

  private fun generateToken(extraClaims: Map<String, Any>, sub: String, iss: String): String {
    return Jwts.builder()
      .claims(extraClaims)
      .subject(sub)
      .issuer(iss)
      .issuedAt(Date(System.currentTimeMillis()))
      .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 soat
      .signWith(signingKey)
      .compact()
  }

  private fun isTokenExpired(token: String): Boolean = extractExpiration(token).before(Date())

  private fun extractExpiration(token: String): Date = extractClaim(token, Claims::getExpiration)

  private fun extractAllClaims(token: String): Claims {
    return Jwts.parser()
      .verifyWith(signingKey)
      .build()
      .parseSignedClaims(token)
      .payload
  }
}
