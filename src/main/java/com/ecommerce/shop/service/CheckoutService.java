package com.ecommerce.shop.service;

import com.ecommerce.shop.dto.PaymentInfo;
import com.ecommerce.shop.dto.Purchase;
import com.ecommerce.shop.dto.PurchaseResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface CheckoutService {

    PurchaseResponse placeOrder(Purchase purchase);

    PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException;

}
