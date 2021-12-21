package com.gal.coupon2.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gal.coupon2.security.Information;
import com.gal.coupon2.security.TokenManager;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenExpirationDailyJob {
	
	private final TokenManager tokenManager;
	
//	The thread can delete the expired tokens every 24 hours because the expired coupons won't work and will be deleted once the client tries to use then => see the TokenManager class.
//	When we want to check this thread work, we can schedule this thread to work every 20 seconds, 
//	and we can also change the method to delete tokens that are 1 minute old instead of 30 minutes old.
	
	@Scheduled(fixedRate = 1000*60*60*24)   
//	@Scheduled(fixedRate = 1000*20)			// we can switch to this option for testings purpose.
	public void removeExpiredTokens() {

		List<String> expiredTokens = new ArrayList<>();
		for (Entry<String, Information> entry : tokenManager.getMap().entrySet()) {
			if (System.currentTimeMillis() - entry.getValue().getTimestamp() >  1000 * 60 * 30) {
				expiredTokens.add(entry.getKey());
			}
		}
		for (String token : expiredTokens) {
			tokenManager.getMap().remove(token);
		}		
	}

}



// For testing purpose we can print all tokens before deleting the expired ones and then after the deletion using this code:

//for (Entry<String, Information> entry : tokenManager.getMap().entrySet()) {
//	System.out.println(entry);
//}
