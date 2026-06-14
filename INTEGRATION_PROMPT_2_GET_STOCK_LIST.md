# Prompt 2: Integrate "Get Opening Stock Items List" with UI

## Overview
Integrate the **Opening Stock Items List** dashboard with the backend API to fetch and display all active opening stock records with summary cards showing totals.

## API Endpoint
- **Method**: `GET`
- **URL**: `/api/item-master/opening-stock-report`
- **Content-Type**: `application/json`
- **Authentication**: No specific token required (if no auth configured)

## Request
No request body. Optional query parameters (for future enhancement):
```
GET /api/item-master/opening-stock-report?category=Tablet&status=active
```

## Response Body Schema (Success - 200)
```json
{
  "items": [
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
    },
    {
      "openingStockId": 2,
      "itemId": 1,
      "itemCode": "BT1823",
      "itemName": "Paracetamol 500mg",
      "batchCode": "BT1823",
      "openingStock": 120,
      "purchasePrice": 12.00,
      "salePrice": 15.00,
      "mrp": 18.00,
      "totalAmount": 1440.00
    },
    {
      "openingStockId": 3,
      "itemId": 2,
      "itemCode": "SY2201",
      "itemName": "Cough Syrup",
      "batchCode": "SY2201",
      "openingStock": 45,
      "purchasePrice": 85.00,
      "salePrice": 110.00,
      "mrp": 125.00,
      "totalAmount": 3825.00
    }
  ],
  "totalItems": 12,
  "totalQuantity": 798,
  "overallStockValue": 24168.00
}
```

## Response Field Descriptions
- **items**: Array of opening stock records (active & not deleted)
- **totalItems**: Distinct count of unique itemIds (like top card "TOTAL ITEMS")
- **totalQuantity**: Sum of all opening_stock quantities (like top card "TOTAL QUANTITY")
- **overallStockValue**: Sum of all totalAmount values (like top card "STOCK VALUE")

## Error Responses

### 1. No Data (200 - Empty Response)
```json
{
  "items": [],
  "totalItems": 0,
  "totalQuantity": 0,
  "overallStockValue": 0.00
}
```

### 2. Server Error (500)
```json
{
  "timestamp": "2026-06-14T10:30:00",
  "status": 500,
  "message": "An error occurred while fetching opening stock report",
  "error": "Internal Server Error"
}
```

## Integration Steps

### 1. Display Summary Cards (Top Section)
Create four cards that display the summary data:

```
┌─────────────────────────────────────────────────────────┐
│  Total Items   │  Total Quantity  │  Stock Value  │ Alerts │
│      12        │       798        │   ₹24,168    │    5    │
│  unique SKUs   │  units in stock  │ @cost price  │ low/exp │
└─────────────────────────────────────────────────────────┘
```

Mapping:
- **TOTAL ITEMS**: `response.totalItems`
- **TOTAL QUANTITY**: `response.totalQuantity`
- **STOCK VALUE**: `response.overallStockValue` (format as currency with ₹ prefix)
- **ALERTS**: Count items with expiry < 30 days or stock < threshold (future enhancement)

### 2. Display Data Table
Create a table with columns:
| # | Item Code | Item Name | Batch No | Quantity | Rate | Amount | Expiry | Action |
|---|-----------|-----------|----------|----------|------|--------|--------|--------|
| 1 | VC9888 | Vitamin Capsules | BATCH-2024-001 | 75 | 35.00 | 2625.00 | 2027-12-31 | Edit/Delete |

Mapping:
- **Item Code**: `items[i].itemCode`
- **Item Name**: `items[i].itemName`
- **Batch No**: `items[i].batchCode`
- **Quantity**: `items[i].openingStock`
- **Rate**: `items[i].purchasePrice`
- **Amount**: `items[i].totalAmount` (already calculated)
- **Expiry**: Fetch from DB (future - currently in entity but not in response DTO)

### 3. Frontend Fetch Implementation (JavaScript/React)
```javascript
const fetchOpeningStockList = async () => {
  try {
    setLoading(true);
    const response = await fetch('/api/item-master/opening-stock-report', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error('Failed to fetch opening stock report');
    }

    const data = await response.json();
    setReportData(data);
    return data;
  } catch (error) {
    console.error('Error fetching stock report:', error);
    setError(error.message);
    throw error;
  } finally {
    setLoading(false);
  }
};

// Call on component mount
useEffect(() => {
  fetchOpeningStockList();
}, []);
```

### 4. Render Summary Cards (React Example)
```javascript
const SummaryCards = ({ reportData }) => {
  if (!reportData) return null;

  return (
    <div className="summary-cards">
      <Card>
        <Icon>📦</Icon>
        <Label>Total Items</Label>
        <Value>{reportData.totalItems}</Value>
        <Subtitle>unique SKUs</Subtitle>
      </Card>

      <Card>
        <Icon>📊</Icon>
        <Label>Total Quantity</Label>
        <Value>{reportData.totalQuantity}</Value>
        <Subtitle>units in stock</Subtitle>
      </Card>

      <Card>
        <Icon>💰</Icon>
        <Label>Stock Value</Label>
        <Value>₹{formatCurrency(reportData.overallStockValue)}</Value>
        <Subtitle>@cost price</Subtitle>
      </Card>

      <Card>
        <Icon>⚠️</Icon>
        <Label>Alerts</Label>
        <Value>{calculateAlerts(reportData.items)}</Value>
        <Subtitle>low stock / expiry</Subtitle>
      </Card>
    </div>
  );
};
```

### 5. Render Data Table (React Example)
```javascript
const OpeningStockTable = ({ items, loading }) => {
  if (loading) return <Spinner />;
  if (!items || items.length === 0) return <EmptyState message="No opening stock records found" />;

  return (
    <Table>
      <TableHead>
        <Row>
          <Cell>#</Cell>
          <Cell>Item Code</Cell>
          <Cell>Item Name</Cell>
          <Cell>Batch No</Cell>
          <Cell>Quantity</Cell>
          <Cell>Rate</Cell>
          <Cell>Amount</Cell>
          <Cell>Action</Cell>
        </Row>
      </TableHead>
      <TableBody>
        {items.map((item, index) => (
          <Row key={item.openingStockId}>
            <Cell>{index + 1}</Cell>
            <Cell>{item.itemCode}</Cell>
            <Cell>{item.itemName}</Cell>
            <Cell>{item.batchCode}</Cell>
            <Cell>{item.openingStock}</Cell>
            <Cell>₹{item.purchasePrice.toFixed(2)}</Cell>
            <Cell>₹{item.totalAmount.toFixed(2)}</Cell>
            <Cell>
              <Button onClick={() => editStock(item.openingStockId)}>Edit</Button>
              <Button onClick={() => deleteStock(item.openingStockId)}>Delete</Button>
            </Cell>
          </Row>
        ))}
      </TableBody>
    </Table>
  );
};
```

### 6. Formatting Helpers
```javascript
// Format currency values
const formatCurrency = (value) => {
  return parseFloat(value).toLocaleString('en-IN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
};

// Format quantity with comma separator
const formatQuantity = (qty) => {
  return qty.toLocaleString('en-IN');
};

// Calculate alert count (example)
const calculateAlerts = (items) => {
  let count = 0;
  const today = new Date();
  const thirtyDaysFromNow = new Date(today.getTime() + 30 * 24 * 60 * 60 * 1000);
  
  items.forEach(item => {
    // Check if low stock (< 20 units)
    if (item.openingStock < 20) count++;
    
    // Check if expiry < 30 days (if expiryDate available)
    // if (new Date(item.expiryDate) < thirtyDaysFromNow) count++;
  });
  
  return count;
};
```

### 7. Pagination (Optional - for large datasets)
```javascript
const [pageSize, setPageSize] = useState(10);
const [currentPage, setCurrentPage] = useState(0);

const paginatedItems = reportData.items.slice(
  currentPage * pageSize,
  (currentPage + 1) * pageSize
);

const totalPages = Math.ceil(reportData.items.length / pageSize);
```

### 8. Sorting & Filtering (Optional)
```javascript
const [sortBy, setSortBy] = useState('itemName');
const [filterCategory, setFilterCategory] = useState('all');

const sortedItems = [...reportData.items].sort((a, b) => {
  if (sortBy === 'itemName') return a.itemName.localeCompare(b.itemName);
  if (sortBy === 'quantity') return b.openingStock - a.openingStock;
  if (sortBy === 'amount') return b.totalAmount - a.totalAmount;
  return 0;
});
```

## Business Rules
1. **Auto-Refresh**: Consider implementing auto-refresh every 5-10 minutes
2. **Data Consistency**: totalItems, totalQuantity, overallStockValue are calculated server-side
3. **Active Records Only**: Only shows records where is_active=true and is_deleted=false
4. **Currency Format**: Use Indian Rupee (₹) with 2 decimal places
5. **Empty State**: Show friendly message when no records exist

## Performance Notes
- Response includes full list of items (no pagination on server currently)
- For large datasets (>1000 items), implement pagination/filtering on backend
- Consider caching response with 5-minute TTL to reduce DB queries

## Future Enhancements
1. Add expiryDate to response DTO for expiry tracking
2. Add pagination/filter query parameters
3. Add group-by-item aggregation (multiple batches per item)
4. Add real-time alerts for low stock
5. Add export to CSV functionality
6. Add batch-level expiry date comparison

## Related Endpoints
- **POST** `/api/item-master/opening-stock` — Create new opening stock record
- **GET** `/api/item-master/{itemId}` — Get item details
- **PUT** `/api/item-master/{itemId}` — Update item (future)
