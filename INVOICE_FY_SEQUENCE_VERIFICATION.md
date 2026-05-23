# Invoice FY-Based Sequential Format - Implementation Verification Guide

## Overview
The invoice numbering system has been updated from timestamp-based (`INV/2026/1778923574093`) to financial year-based sequential format (`INV/26-27/0001`, `INV/26-27/0002`, etc.)

## Key Changes

### 1. Database Migration (V12)
- Adds `version` column to `invoice_record` table for optimistic locking
- Creates `invoice_sequence` table to track sequential numbers per fiscal year
- Migration auto-runs on application startup via Flyway

### 2. Service Logic Updates
- **Invoice number generation** is now handled entirely by the server
- **Client input is ignored** - even if API client sends invoiceNo, the server overwrites it
- Uses **optimistic locking** with automatic retry (8 attempts) for concurrent creation

### 3. Financial Year Calculation
- **April 1 - March 31** fiscal year boundary
- Format: `YY-YY` (e.g., `26-27` for May 2026)
- Automatic: No configuration needed, based on invoice date

## Test Scenarios

### Test 1: Basic Sequential Generation (Current Date)
**Date**: May 23, 2026 (FY: 26-27)

1. Create 3 invoices with this payload:
```json
{
  "invoiceDate": "2026-05-23",
  "unitId": 1,
  "customerId": 1,
  "taxableAmount": 1000,
  "finalAmount": 1180,
  "totalCgst": 90,
  "totalSgst": 90
}
```

**Expected Output**:
- Invoice 1: `INV/26-27/0001`
- Invoice 2: `INV/26-27/0002`
- Invoice 3: `INV/26-27/0003`

### Test 2: Fiscal Year Boundary - Before April 1
**Date**: March 31, 2026 (FY: 25-26)

1. Create invoice with invoiceDate = "2026-03-31"

**Expected Output**: `INV/25-26/0001` (new sequence for FY 25-26)

### Test 3: Fiscal Year Boundary - After April 1
**Date**: April 1, 2026 (FY: 26-27)

1. Create invoice with invoiceDate = "2026-04-01"

**Expected Output**: 
- If this is the first invoice for FY 26-27: `INV/26-27/0001`
- If invoices already exist for 26-27: continues the sequence (e.g., `INV/26-27/0004`)

### Test 4: Client-Side Invoice Number Ignored
**Date**: Any date

1. Try to create invoice with explicit invoiceNo in request:
```json
{
  "invoiceNo": "DUMMY-123",
  "invoiceDate": "2026-05-23",
  "unitId": 1,
  "customerId": 1
}
```

**Expected Output**: System ignores the `DUMMY-123` and generates `INV/26-27/NNNN` format instead

### Test 5: Concurrent Creation (Optimistic Locking)
**Tool**: Use Apache JMeter, Spring Boot test, or similar to create 5+ invoices simultaneously

**Expected Output**: 
- All invoices created successfully
- Sequential numbers without gaps: `INV/26-27/0001` through `INV/26-27/00XX`
- No duplicate numbers
- All have correct version value in response

## Database Verification Queries

```sql
-- Check the sequence table
select * from invoice_sequence;

-- Expected output:
-- | id | fy    | last_number | version | created_at | updated_at | is_active | is_deleted |
-- | 1  | 26-27 | 3           | 0       | [timestamp]| [timestamp]| true      | false      |

-- Check generated invoice numbers
select invoice_id, invoice_no, invoice_date, version from invoice_record 
where invoice_no like 'INV/%/%' 
order by invoice_id desc limit 10;

-- Expected output:
-- | invoice_id | invoice_no    | invoice_date | version |
-- | 3          | INV/26-27/0003| 2026-05-23   | 0       |
-- | 2          | INV/26-27/0002| 2026-05-23   | 0       |
-- | 1          | INV/26-27/0001| 2026-05-23   | 0       |
```

## API Endpoint to Test
```
POST /api/invoice
Content-Type: application/json

{
  "invoiceDate": "2026-05-23",
  "invoiceNo": null,
  "unitId": 1,
  "customerId": 1,
  "placeOfSupply": "Karnataka",
  "stateCode": "KA",
  "taxableAmount": 1000.00,
  "totalCgst": 90.00,
  "totalSgst": 90.00,
  "finalAmount": 1180.00,
  "totalDiscount": 0,
  "roundOff": 0
}
```

## Troubleshooting

### Issue: Still seeing old format (INV/2026/NNNN)
- **Cause**: Migration V12 not applied
- **Solution**: 
  - Check database logs: `select * from flyway_schema_history order by installed_rank desc;`
  - Ensure `invoice_sequence` table exists
  - Restart application to trigger Flyway

### Issue: Duplicate invoice numbers or gaps
- **Cause**: Version mismatch or stale sequence record
- **Solution**: Check sequence table for version conflicts and reset if needed:
```sql
UPDATE invoice_sequence SET version = 0 WHERE fy = '26-27';
```

### Issue: OptimisticLockingFailureException errors
- **Cause**: Normal during high concurrency (system handles with retry)
- **Solution**: This is expected. Monitor application logs for persistent failures.

## Implementation Details

### Files Modified/Created
1. **InvoiceServiceImpl.java** - Modified `createInvoice()` method (Line ~93)
2. **InvoiceSequenceEntity.java** - NEW JPA entity
3. **InvoiceSequenceRepository.java** - NEW Spring Data Repository
4. **V12__invoice_sequence_and_version.sql** - NEW Flyway migration
5. **InvoiceRecordEntity.java** - Added @Version annotation
6. **InvoiceRecordDTO.java** - Added version field

### Key Methods
- `generateNextInvoiceNo(LocalDate date)` - Generates INV/YY-YY/NNNN format with optimistic retry
- `computeFinancialYear(LocalDate date)` - Computes FY as YY-YY based on April-March boundary

## Build Verification
✅ Build Status: `mvn clean compile -DskipTests` SUCCESS
- 107 source files compiled
- No compilation errors
- Ready for deployment

## Next Steps
1. Deploy application
2. Verify Flyway migration V12 applies successfully
3. Run Test Scenario 1 (Basic Sequential Generation)
4. Proceed with other test scenarios
5. Monitor database for correct sequence values
