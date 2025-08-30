import React, { useState } from 'react';
import {
  Typography,
  Button,
  TextField,
  Box,
  Alert,
  Card,
  CardContent,
  Stack,
  Chip,
} from '@mui/material';
import {
  Download,
  GetApp,
  TableChart,
} from '@mui/icons-material';
import { apiService } from '../services/api';

interface ExportFormData {
  tableName: string;
}

const ExportManager: React.FC = () => {
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [exportFormData, setExportFormData] = useState<ExportFormData>({
    tableName: '',
  });

  const availableTables = [
    { name: 'items', description: 'All items in the system' },
    { name: 'inventory', description: 'Inventory items with stock and capacity' },
    { name: 'distributors', description: 'All distributors' },
    { name: 'distributor_prices', description: 'Distributor prices for items' },
  ];

  const handleExport = async (tableName: string) => {
    setLoading(true);
    try {
      const response = await apiService.exportTableToCsv(tableName);
      
      // Create blob and download file
      const blob = new Blob([response.data], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `${tableName}.csv`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      
      setSuccess(`${tableName}.csv downloaded successfully!`);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.error || `Failed to export ${tableName}`);
    } finally {
      setLoading(false);
    }
  };

  const handleCustomExport = async () => {
    if (!exportFormData.tableName.trim()) {
      setError('Please enter a table name');
      return;
    }
    await handleExport(exportFormData.tableName.trim());
  };

  const resetDatabase = async () => {
    try {
      await apiService.resetDatabase();
      setSuccess('Database reset successfully!');
      setError(null);
    } catch (err: any) {
      setError('Failed to reset database');
    }
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1">
          Export & Utilities
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess(null)}>
          {success}
        </Alert>
      )}

      {/* Export Tables Section */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Export Tables to CSV
          </Typography>
          <Typography color="textSecondary" sx={{ mb: 3 }}>
            Download data from available tables as CSV files
          </Typography>
          
          <Stack spacing={2}>
            {availableTables.map((table) => (
              <Box
                key={table.name}
                display="flex"
                justifyContent="space-between"
                alignItems="center"
                sx={{
                  p: 2,
                  border: 1,
                  borderColor: 'divider',
                  borderRadius: 1,
                  '&:hover': {
                    backgroundColor: 'action.hover',
                  },
                }}
              >
                <Box>
                  <Typography variant="subtitle1" sx={{ fontWeight: 'medium' }}>
                    {table.name}
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    {table.description}
                  </Typography>
                </Box>
                <Button
                  variant="outlined"
                  startIcon={<Download />}
                  onClick={() => handleExport(table.name)}
                  disabled={loading}
                >
                  Export
                </Button>
              </Box>
            ))}
          </Stack>
        </CardContent>
      </Card>

      {/* Custom Export Section */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Custom Table Export
          </Typography>
          <Typography color="textSecondary" sx={{ mb: 3 }}>
            Export any table by name (for advanced users)
          </Typography>
          
          <Box display="flex" gap={2} alignItems="flex-end">
            <TextField
              label="Table Name"
              value={exportFormData.tableName}
              onChange={(e) => setExportFormData({ tableName: e.target.value })}
              placeholder="Enter table name"
              sx={{ flex: 1 }}
            />
            <Button
              variant="contained"
              startIcon={<GetApp />}
              onClick={handleCustomExport}
              disabled={loading}
            >
              Export
            </Button>
          </Box>
        </CardContent>
      </Card>

      {/* Database Utilities Section */}
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Database Utilities
          </Typography>
          <Typography color="textSecondary" sx={{ mb: 3 }}>
            Database management and maintenance tools
          </Typography>
          
          <Box>
            <Button
              variant="outlined"
              color="warning"
              onClick={resetDatabase}
              startIcon={<TableChart />}
              sx={{ mr: 2 }}
            >
              Reset Database
            </Button>
            <Chip 
              label="Warning: This will delete all data" 
              color="warning" 
              variant="outlined" 
              size="small"
            />
          </Box>
        </CardContent>
      </Card>

      {/* Info Section */}
      <Card sx={{ mt: 3, backgroundColor: 'grey.50' }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Export Information
          </Typography>
          <Typography variant="body2" color="textSecondary">
            • CSV files will be automatically downloaded to your default download folder
            <br />
            • Valid table names: items, inventory, distributors, distributor_prices
            <br />
            • Custom table export allows you to export any existing table in the database
            <br />
            • Reset database will restore the database to its initial state with sample data
          </Typography>
        </CardContent>
      </Card>
    </Box>
  );
};

export default ExportManager;
