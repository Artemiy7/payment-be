package com.ecommerce.shop.service;

import com.ecommerce.shop.constant.Constants;
import com.ecommerce.shop.dao.CustomerRepository;
import com.ecommerce.shop.dto.PaymentInfo;
import com.ecommerce.shop.dto.Purchase;
import com.ecommerce.shop.dto.PurchaseResponse;
import com.ecommerce.shop.entity.Customer;
import com.ecommerce.shop.entity.Order;
import com.ecommerce.shop.entity.OrderItem;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private CustomerRepository customerRepository;

    public CheckoutServiceImpl(CustomerRepository customerRepository,
                               @Value("${stripe.key.secret}") String secretKey) {
        this.customerRepository = customerRepository;
        Stripe.apiKey = secretKey;
    }

    @Override
    @Transactional
    public PurchaseResponse placeOrder(Purchase purchase) {

        Order order = purchase.getOrder();
        String orderTrackingNumber = generateOrderTrackingNumber();
        order.setOrderTrackingNumber(orderTrackingNumber);

        Set<OrderItem> orderItems = purchase.getOrderItems();
        orderItems.forEach(item -> order.add(item));

        order.setBillingAddress(purchase.getBillingAddress());
        order.setShippingAddress(purchase.getShippingAddress());

        Customer customer = purchase.getCustomer();

        String theEmail = customer.getEmail();

        Customer customerFromDB = customerRepository.findByEmail(theEmail);

        if (customerFromDB != null)
            customer = customerFromDB;

        customer.add(order);
        customerRepository.save(customer);
        return new PurchaseResponse(orderTrackingNumber);
    }

    @Override
    public PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException {

        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add(Constants.CARD);

        Map<String, Object> params = new HashMap<>();
        params.put(Constants.AMOUNT, paymentInfo.getAmount());
        params.put(Constants.CURRENCY, paymentInfo.getCurrency());
        params.put(Constants.PAYMENT_METHOD_TYPES, paymentMethodTypes);
        params.put(Constants.DESCRIPTION, "purchase");
        params.put(Constants.RECEIPT_EMAIL, paymentInfo.getReceiptEmail());

        return PaymentIntent.create(params);
    }

    private String generateOrderTrackingNumber() {
        return UUID.randomUUID().toString();
    }
}









