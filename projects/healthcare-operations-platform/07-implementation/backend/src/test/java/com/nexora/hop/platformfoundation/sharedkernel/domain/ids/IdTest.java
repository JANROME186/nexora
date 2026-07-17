package com.nexora.hop.platformfoundation.sharedkernel.domain.ids;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdTest {

    @Test
    void testBranchId() {
        BranchId id1 = new BranchId("1");
        BranchId id2 = new BranchId("1");
        BranchId id3 = new BranchId("2");
        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertTrue(id1.toString().contains("1"));
        assertEquals("1", id1.value());
    }

    @Test
    void testLaboratoryId() {
        LaboratoryId id1 = new LaboratoryId("1");
        LaboratoryId id2 = new LaboratoryId("1");
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertTrue(id1.toString().contains("1"));
        assertEquals("1", id1.value());
    }

    @Test
    void testOrderId() {
        OrderId id1 = new OrderId("1");
        OrderId id2 = new OrderId("1");
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertTrue(id1.toString().contains("1"));
        assertEquals("1", id1.value());
    }

    @Test
    void testPatientId() {
        PatientId id1 = new PatientId("1");
        PatientId id2 = new PatientId("1");
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertTrue(id1.toString().contains("1"));
        assertEquals("1", id1.value());
    }

    @Test
    void testResultId() {
        ResultId id1 = new ResultId("1");
        ResultId id2 = new ResultId("1");
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertTrue(id1.toString().contains("1"));
        assertEquals("1", id1.value());
    }

    @Test
    void testSampleId() {
        SampleId id1 = new SampleId("1");
        SampleId id2 = new SampleId("1");
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertTrue(id1.toString().contains("1"));
        assertEquals("1", id1.value());
    }

    @Test
    void testTenantId() {
        TenantId id1 = new TenantId("1");
        TenantId id2 = new TenantId("1");
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertTrue(id1.toString().contains("1"));
        assertEquals("1", id1.value());
    }

    @Test
    void testUserId() {
        UserId id1 = new UserId("1");
        UserId id2 = new UserId("1");
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertTrue(id1.toString().contains("1"));
        assertEquals("1", id1.value());
    }
}
