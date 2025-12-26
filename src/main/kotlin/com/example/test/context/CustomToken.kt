package com.example.test.context

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class CustomAuthenticationToken(
  private val principal: UserPrincipal,
  private val authorities: Collection<GrantedAuthority> = emptyList()
) : Authentication {
  override fun getName(): String = principal.user.firstName ?: ""
  override fun getAuthorities(): Collection<GrantedAuthority> = authorities
  override fun getCredentials(): Any? = null
  override fun getDetails(): Any? = null
  override fun getPrincipal(): Any = principal
  override fun isAuthenticated(): Boolean = true
  override fun setAuthenticated(isAuthenticated: Boolean) {}
}
