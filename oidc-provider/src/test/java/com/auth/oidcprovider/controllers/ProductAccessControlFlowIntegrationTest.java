package com.auth.oidcprovider.controllers;

import com.auth.oidcprovider.config.TestSecurityConfig;
import com.auth.oidcprovider.dto.ApiResponse;
import com.auth.oidcprovider.models.Product;
import com.auth.oidcprovider.models.User;
import com.auth.oidcprovider.repositories.ProductRepository;
import com.auth.oidcprovider.util.JwtTestUtil;
import com.auth.oidcprovider.util.TestDataInitializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the product access control flow.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class ProductAccessControlFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestDataInitializer testDataInitializer;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JwtTestUtil jwtTestUtil;

    private User adminUser;
    private User salesManagerUser;
    private User crmRepUser;

    @BeforeEach
    void setUp() {
        // Initialize test products and their roles
        testDataInitializer.initializeProducts();
        testDataInitializer.setupProductRoles();

        // Create test users with different roles
        adminUser = testDataInitializer.createTestUser("admin", "ADMIN");
        salesManagerUser = testDataInitializer.createTestUser("sales_manager", "SALES_MANAGER");
        crmRepUser = testDataInitializer.createTestUser("crm_rep", "CRM_REP");
    }

    @AfterEach
    void tearDown() {
        testDataInitializer.cleanupTestData();
    }

    /**
     * Tests that an admin user can access all products.
     */
    @Test
    void adminCanAccessAllProducts() throws Exception {
        // Generate admin token
        String token = jwtTestUtil.generateAdminToken(adminUser.getId(), adminUser.getUsername());

        // Test access to all products endpoint
        MvcResult result = mockMvc.perform(get("/api/products/all")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[*].name", containsInAnyOrder("iPhone", "iPad", "MacBook Pro")))
                .andReturn();

        // Test access to specific products
        mockMvc.perform(get("/api/products/iphone")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("iPhone")));

        mockMvc.perform(get("/api/products/ipad")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("iPad")));

        mockMvc.perform(get("/api/products/macbook")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("MacBook Pro")));
    }

    /**
     * Tests that a sales manager can access iPhone and iPad but not MacBook Pro.
     */
    @Test
    void salesManagerCanAccessLimitedProducts() throws Exception {
        // Generate sales manager token
        String token = jwtTestUtil.generateSalesManagerToken(salesManagerUser.getId(), salesManagerUser.getUsername());

        // Test accessible products endpoint
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[*].name", containsInAnyOrder("iPhone", "iPad")))
                .andReturn();

        // Test access to authorized products
        mockMvc.perform(get("/api/products/iphone")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("iPhone")));

        mockMvc.perform(get("/api/products/ipad")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("iPad")));

        // Test access to unauthorized product
        mockMvc.perform(get("/api/products/macbook")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that a CRM rep can only access iPhone.
     */
    @Test
    void crmRepCanOnlyAccessIPhone() throws Exception {
        // Generate CRM rep token
        String token = jwtTestUtil.generateCrmRepToken(crmRepUser.getId(), crmRepUser.getUsername());

        // Test accessible products endpoint
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", is("iPhone")))
                .andReturn();

        // Test access to authorized product
        mockMvc.perform(get("/api/products/iphone")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("iPhone")));

        // Test access to unauthorized products
        mockMvc.perform(get("/api/products/ipad")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/products/macbook")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that an admin can create a new product.
     */
    @Test
    void adminCanCreateProduct() throws Exception {
        // Generate admin token
        String token = jwtTestUtil.generateAdminToken(adminUser.getId(), adminUser.getUsername());

        // Create new product
        Product newProduct = new Product("AppleWatch", "Apple Watch Series 7", 399.99);
        
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("AppleWatch")));

        // Verify product was created
        assertTrue(productRepository.findByName("AppleWatch").isPresent());
    }

    /**
     * Tests that a non-admin user cannot create a product.
     */
    @Test
    void nonAdminCannotCreateProduct() throws Exception {
        // Generate sales manager token
        String token = jwtTestUtil.generateSalesManagerToken(salesManagerUser.getId(), salesManagerUser.getUsername());

        // Attempt to create new product
        Product newProduct = new Product("AppleWatch", "Apple Watch Series 7", 399.99);
        
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isForbidden());

        // Verify product was not created
        assertFalse(productRepository.findByName("AppleWatch").isPresent());
    }

    /**
     * Tests that an admin can add and remove roles from a product.
     */
    @Test
    void adminCanManageProductRoles() throws Exception {
        // Generate admin token
        String token = jwtTestUtil.generateAdminToken(adminUser.getId(), adminUser.getUsername());

        // Get MacBook Pro
        Product macbookPro = productRepository.findByName("MacBook Pro").orElseThrow();
        
        // Initially, CRM_REP should not have access to MacBook Pro
        mockMvc.perform(get("/api/products/macbook")
                .header("Authorization", "Bearer " + jwtTestUtil.generateCrmRepToken(crmRepUser.getId(), crmRepUser.getUsername())))
                .andExpect(status().isUnauthorized());
        
        // Add CRM_REP role to MacBook Pro
        mockMvc.perform(post("/api/products/" + macbookPro.getId() + "/roles/CRM_REP")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
        
        // Now CRM_REP should have access to MacBook Pro
        mockMvc.perform(get("/api/products/macbook")
                .header("Authorization", "Bearer " + jwtTestUtil.generateCrmRepToken(crmRepUser.getId(), crmRepUser.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("MacBook Pro")));
        
        // Remove CRM_REP role from MacBook Pro
        mockMvc.perform(delete("/api/products/" + macbookPro.getId() + "/roles/CRM_REP")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
        
        // CRM_REP should no longer have access to MacBook Pro
        mockMvc.perform(get("/api/products/macbook")
                .header("Authorization", "Bearer " + jwtTestUtil.generateCrmRepToken(crmRepUser.getId(), crmRepUser.getUsername())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that a non-admin user cannot manage product roles.
     */
    @Test
    void nonAdminCannotManageProductRoles() throws Exception {
        // Generate sales manager token
        String token = jwtTestUtil.generateSalesManagerToken(salesManagerUser.getId(), salesManagerUser.getUsername());

        // Get MacBook Pro
        Product macbookPro = productRepository.findByName("MacBook Pro").orElseThrow();
        
        // Attempt to add CRM_REP role to MacBook Pro
        mockMvc.perform(post("/api/products/" + macbookPro.getId() + "/roles/CRM_REP")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
} 