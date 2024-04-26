package com.secureAPI.secureAPI.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private  static final String SECRET_KEY = "4wX7yoZsRW8yRLqYaGRNUk4PAdKSWd8SXySGyE8yXgADJbu3j3g59HuKy5aBRKoZxzzhErY9ygZmrgkj45DEZJdYdHJnjWeZrCYfss9LR5qLBf3eXU3udk93pAgtE6Q3Dq3qZzrfz4gX2Csj9UQ6743zqSH36k3KdbH7j4SP4b9wkb6jYxXncn4ScZfbG3dfETtQjSU4NDksJq6pQjqXpKhxyx9af3t6ukLKGaLyFjdJdbU84oThBMf6ehZf36DC";

    public String generateToken( UserDetails userDetails ){
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extractClaims,
            UserDetails userDetails
    ){
        return  Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 600 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String userName = extractUserName(token);
        return  userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims :: getExpiration);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims :: getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        final  Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }
    private Claims extractAllClaims(String token){

        return Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
