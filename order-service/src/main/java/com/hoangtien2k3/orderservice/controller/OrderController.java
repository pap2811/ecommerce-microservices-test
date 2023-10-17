package com.hoangtien2k3.orderservice.controller;

import com.hoangtien2k3.orderservice.dto.OrderDto;
import com.hoangtien2k3.orderservice.dto.response.collection.DtoCollectionResponse;
import com.hoangtien2k3.orderservice.security.JwtValidate;
import com.hoangtien2k3.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private final OrderService orderService;

    @Autowired
    private final JwtValidate jwtValidate;


    @GetMapping
    public ResponseEntity<DtoCollectionResponse<OrderDto>> findAll() {
        log.info("OrderDto List, controller; fetch all orders");
        return ResponseEntity.ok(new DtoCollectionResponse<>(this.orderService.findAll()));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize(value = "hasAuthority('USER')")
    public ResponseEntity<OrderDto> findById(@PathVariable("orderId")
                                             @NotBlank(message = "Input must not be blank")
                                             @Valid final Integer orderId) {

        log.info("OrderDto, resource; fetch order by id");
        return ResponseEntity.ok(this.orderService.findById(orderId));
    }

    @PostMapping
    @PreAuthorize(value = "hasAuthority('ADMIN')")
    public ResponseEntity<OrderDto> save(@RequestHeader(name = "Authorization") String authorization,
                                         @RequestBody @NotNull(message = "Input must not be NULL")
                                         @Valid final OrderDto orderDto) {

        if (jwtValidate.validateTokenUserService(authorization)) {
            log.info("OrderDto, resource; save order");
            return ResponseEntity.ok(this.orderService.save(orderDto));
        }
        return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).build();
    }

    @PutMapping
    public ResponseEntity<OrderDto> update(@RequestBody
                                           @NotNull(message = "Input must not be NULL")
                                           @Valid final OrderDto orderDto) {
        log.info("OrderDto, resource; update order");
        return ResponseEntity.ok(this.orderService.update(orderDto));
    }

    @PutMapping("/{orderId}")
    @PreAuthorize(value = "hasAuthority('ADMIN')")
    public ResponseEntity<OrderDto> update(@RequestHeader(name = "Authorization") String authorization,
                                           @PathVariable("orderId")
                                           @NotBlank(message = "Input must not be blank")
                                           @Valid final Integer orderId,
                                           @RequestBody
                                           @NotNull(message = "Input must not be NULL")
                                           @Valid final OrderDto orderDto) {
        if (jwtValidate.validateTokenUserService(authorization)) {
            log.info("OrderDto, resource; update order with orderId");
            return ResponseEntity.ok(this.orderService.update(orderId, orderDto));
        }
        return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).build();
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("orderId") final Integer orderId) {
        log.info("Boolean, resource; delete order by id");
        this.orderService.deleteById(orderId);
        return ResponseEntity.ok(true);
    }

}
