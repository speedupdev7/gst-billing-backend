# Prompt 1: Integrate "Add Opening Stock Entry" with UI

## Overview
Integrate the **Add Opening Stock Entry** form with the backend API to persist opening stock records for existing items.

## API Endpoint
- **Method**: `POST`
- **URL**: `/api/item-master/opening-stock`
- **Content-Type**: `application/json`

## Request Body Schema
```json
{
  "itemId": 3,                              // Long | Required (OR provide itemName)
  "itemName": "Vitamin Capsules",           // String | Optional (use if itemId not available)
  "batchCode": "BATCH-2024-001",            // String | Required
  "quantity": 75,                           // Integer | Required (quantity/opening stock)
  "purchaseRate": 35.00,                    // BigDecimal | Required
  "sellingRate": 45.00,                     // BigDecimal | Required
  "mrp": 50.00,                             // BigDecimal | Required
  "gstPercent": 18.0,                       // BigDecimal | Optional
  "expiryDate": "2027-12-31",               // LocalDate (yyyy-MM-dd) | Required
  "supplierName": "XYZ Pharma",             // String | Required
  "remarks": "Initial stock entry for Q1"   // String | Optional
}
```

## Response Body Schema (Success - 200)
```json
{
  "openingStockId": 1,
  "itemId": 3,
  "itemCode": "VC9888",
  "itemName": "Vitamin Capsules",
  "batchCode": "BATCH-2024-001",
  "openingStock": 75,
  "purchasePrice": 35.00,
  "salePrice": 45.00,
  "mrp": 50.00,
  "totalAmount": 2625.00
}
```

## Error Responses

### 1. Item Not Found (404)
```json
{
  "timestamp": "2026-06-14T10:30:00",
  "status": 404,
  "message": "Item not found with id: 999",
  "error": "Not Found"
}
```

### 2. Opening Stock Already Exists (400)
```json
{
  "timestamp": "2026-06-14T10:30:00",
  "status": 400,
  "message": "Opening stock already exists for item: Vitamin Capsules",
  "error": "Bad Request"
}
```

### 3. Validation Error - Missing Required Field (400)
```json
{
  "timestamp": "2026-06-14T10:30:00",
  "status": 400,
  "message": "Either itemId or itemName must be provided",
  "error": "Bad Request"
}
```

## Integration Steps

### 1. Form Field Mapping
Map form inputs to request DTO fields:
- **Item Name** dropdown → `itemId` (preferred) or `itemName`
- **Category** → Not sent (use itemId lookup instead)
- **Batch No** → `batchCode`
- **Quantity** → `quantity`
- **Purchase Rate** → `purchaseRate`
- **Selling Rate** → `sellingRate`
- **MRP** → `mrp`
- **GST %** → `gstPercent`
- **Expiry Date** → `expiryDate`
- **Supplier Name** → `supplierName`
- **Remarks** → `remarks`

### 2. Frontend Validation (Before API Call)
```javascript
const validateForm = (formData) => {
  const errors = [];
  
  if (!formData.itemId && !formData.itemName) {
    errors.push("Item must be selected");
  }
  if (!formData.batchCode || formData.batchCode.trim() === "") {
    errors.push("Batch code is required");
  }
  if (!formData.quantity || formData.quantity <= 0) {
    errors.push("Quantity must be greater than 0");
  }
  if (!formData.purchaseRate || formData.purchaseRate <= 0) {
    errors.push("Purchase rate must be greater than 0");
  }
  if (!formData.expiryDate) {
    errors.push("Expiry date is required");
  }
  if (!formData.supplierName || formData.supplierName.trim() === "") {
    errors.push("Supplier name is required");
  }
  
  return errors;
};
```

### 3. API Call Example (JavaScript/React)
```javascript
const addOpeningStock = async (formData) => {
  try {
    const payload = {
      itemId: formData.itemId,
      batchCode: formData.batchCode,
      quantity: parseInt(formData.quantity),
      purchaseRate: parseFloat(formData.purchaseRate),
      sellingRate: parseFloat(formData.sellingRate),
      mrp: parseFloat(formData.mrp),
      gstPercent: formData.gstPercent ? parseFloat(formData.gstPercent) : null,
      expiryDate: formData.expiryDate,
      supplierName: formData.supplierName,
      remarks: formData.remarks
    };

    const response = await fetch('/api/item-master/opening-stock', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message);
    }

    const result = await response.json();
    return result;
  } catch (error) {
    console.error('Error adding opening stock:', error);
    throw error;
  }
};
```

### 4. Success Handling
- Display success toast/notification: "Opening stock added successfully for [itemName]"
- Show response data (openingStockId, totalAmount) in summary
- Clear form fields
- Optionally navigate to stock items list or stay on form

### 5. Error Handling
- Catch specific errors:
  - 404: "Item not found. Please select a valid item."
  - 400: "Opening stock already exists for this item. Please update the existing record."
  - 400: Display validation error message from response
- Display error toast/notification with message
- Disable submit button during request
- Show loading spinner

## Business Rules
1. **Unique Opening Stock**: Only ONE opening stock record per item allowed
2. **Item Must Exist**: Selected item must be an existing master_item entry
3. **Auto-Calculation**: Frontend should display `totalAmount = quantity × purchaseRate` as user types
4. **Date Format**: Use ISO format (yyyy-MM-dd) for expiryDate

## Notes
- `totalAmount` is calculated server-side for audit trail
- `openingStockId` is auto-generated by database
- `itemCode` is fetched from master_item table
- All decimal fields use BigDecimal (2 decimal places in DB)
