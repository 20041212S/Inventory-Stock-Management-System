# Excel File Format Guide

## Part Master Import Format

When importing part master data from Excel, ensure your file follows this format:

### Column Structure (in order):

1. **Part Number** (Required) - Unique identifier, e.g., "PART001"
2. **Part Name** (Required) - Name of the part, e.g., "Steel Rod"
3. **Make or Buy** (Required) - Either "Make" or "Buy"
4. **Category** (Required) - One of: Finished, Semi Finished, Raw Material, Consumables, Fixed Assets
5. **Subcategory** (Optional) - Dependent on category, e.g., "Aluminum", "Stainless Steel", "Mild Steel" for Raw Material
6. **Unit of Measure** (Required) - One of: kg, gm, nos, ml, litre, mm, cm, m, km
7. **Drawing Number** (Optional) - Drawing reference number
8. **Revision Number** (Optional) - Revision identifier
9. **Revised Date** (Optional) - Date in format YYYY-MM-DD or Excel date format
10. **Location** (Optional) - Location name (must exist in database)

### Example Data:

| Part Number | Part Name | Make or Buy | Category | Subcategory | Unit of Measure | Drawing Number | Revision Number | Revised Date | Location |
|-------------|-----------|-------------|----------|-------------|-----------------|----------------|-----------------|--------------|----------|
| PART001 | Steel Rod | Make | Raw Material | Mild Steel | kg | DWG-001 | REV-01 | 2024-01-15 | Warehouse A |
| PART002 | Aluminum Sheet | Buy | Raw Material | Aluminum | kg | DWG-002 | | | Warehouse B |
| PART003 | Finished Product A | Make | Finished | | nos | | | | Production Floor |

## Stock Upload Template Format

When uploading stock data in bulk, use this format:

### Column Structure (in order):

1. **Part Number** (Required) - Must exist in part master
2. **Stock** (Required) - Numeric value for stock quantity
3. **Bin** (Optional) - Bin number/location
4. **Rack** (Optional) - Rack number/location

### Example Data:

| Part Number | Stock | Bin | Rack |
|-------------|-------|-----|------|
| PART001 | 150.5 | BIN-001 | RACK-A1 |
| PART002 | 75.0 | BIN-002 | RACK-A2 |
| PART001 | 50.0 | BIN-001 | RACK-A1 |

### Notes:
- If the same Part Number appears multiple times, stock quantities will be aggregated
- If different bin numbers are detected for the same part, a warning will be shown
- Part Numbers that don't exist in master data will be rejected

## Tips

1. **File Format**: Use .xlsx (Excel 2007+) format
2. **Headers**: First row should contain column headers
3. **Data Types**: 
   - Part Number, Part Name, etc. should be text
   - Stock quantity should be numeric
   - Dates should be in date format or text format YYYY-MM-DD
4. **Empty Cells**: Leave optional fields empty if not applicable
5. **Validation**: Ensure all required fields are filled before importing

