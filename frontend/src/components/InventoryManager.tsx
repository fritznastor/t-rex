import React, { useState, useEffect } from 'react';
import {
  Paper,
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Box,
  Chip,
  Alert,
  Card,
  CardContent,
  IconButton,
  Tooltip,
  Stack,
} from '@mui/material';
import {
  Add,
  Edit,
  Delete,
  Refresh,
  Warning,
  CheckCircle,
  Error,
  Search,
  Clear,
} from '@mui/icons-material';
import { apiService, InventoryItem, Item } from '../services/api';

interface InventoryFormData {
  itemId: string;
  stock: string;
  capacity: string;
}

const InventoryManager: React.FC = () => {
  const [inventory, setInventory] = useState<InventoryItem[]>([]);
  const [items, setItems] = useState<Item[]>([]);
  const [filterView, setFilterView] = useState<'all' | 'out-of-stock' | 'low-stock' | 'overstocked'>('all');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState<InventoryItem | null>(null);
  const [searchId, setSearchId] = useState<string>('');
  const [searchResult, setSearchResult] = useState<InventoryItem | null>(null);
  const [formData, setFormData] = useState<InventoryFormData>({
    itemId: '',
    stock: '',
    capacity: '',
  });

  useEffect(() => {
    fetchInventory();
    fetchItems();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filterView]);

  const fetchInventory = async () => {
    try {
      let response;
      switch (filterView) {
        case 'out-of-stock':
          response = await apiService.getOutOfStockItems();
          break;
        case 'low-stock':
          response = await apiService.getLowStockItems();
          break;
        case 'overstocked':
          response = await apiService.getOverstockedItems();
          break;
        default:
          response = await apiService.getInventory();
      }
      setInventory(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch inventory');
    }
  };

  const fetchItems = async () => {
    try {
      const response = await apiService.getItems();
      setItems(response.data);
    } catch (err) {
      console.error('Failed to fetch items');
    }
  };

  const handleAddItem = async () => {
    try {
      await apiService.addInventoryItem(
        parseInt(formData.itemId),
        parseInt(formData.stock),
        parseInt(formData.capacity)
      );
      setSuccess('Inventory item added successfully!');
      setDialogOpen(false);
      setFormData({ itemId: '', stock: '', capacity: '' });
      fetchInventory();
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to add inventory item');
    }
  };

  const handleUpdateItem = async () => {
    if (!selectedItem) return;
    try {
      await apiService.updateInventoryItem(
        selectedItem.id,  // Use the item ID directly
        formData.stock ? parseInt(formData.stock) : undefined,
        formData.capacity ? parseInt(formData.capacity) : undefined
      );
      setSuccess('Inventory item updated successfully!');
      setEditDialogOpen(false);
      setSelectedItem(null);
      setFormData({ itemId: '', stock: '', capacity: '' });
      fetchInventory();
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to update inventory item');
    }
  };

  const handleDeleteItem = async (id: number) => {
    try {
      await apiService.deleteInventoryItem(id);
      setSuccess('Inventory item deleted successfully!');
      fetchInventory();
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to delete inventory item');
    }
  };

  const openEditDialog = (item: InventoryItem) => {
    setSelectedItem(item);
    setFormData({
      itemId: item.id.toString(),  // Use item.id directly (this is the item ID)
      stock: item.stock.toString(),
      capacity: item.capacity.toString(),
    });
    setEditDialogOpen(true);
  };

  const searchItemById = async () => {
    if (!searchId.trim()) {
      setError('Please enter an item ID');
      return;
    }
    
    try {
      const response = await apiService.getInventoryById(parseInt(searchId));
      if (response.data && Array.isArray(response.data) && response.data.length > 0) {
        setSearchResult(response.data[0]);
        setError(null);
      } else {
        setSearchResult(null);
        setError('No item found with that ID');
      }
    } catch (err: any) {
      setSearchResult(null);
      if (err.response?.status === 404) {
        setError('Item not found');
      } else {
        setError('Failed to search for item');
      }
    }
  };

  const clearSearch = () => {
    setSearchId('');
    setSearchResult(null);
    setError(null);
  };

  const getStockStatus = (item: InventoryItem) => {
    if (item.stock === 0) return { label: 'Out of Stock', color: 'error' as const };
    if (item.stock > item.capacity) return { label: 'Overstocked', color: 'warning' as const };
    if (item.stock < item.capacity * 0.35) return { label: 'Low Stock', color: 'warning' as const };
    return { label: 'Good', color: 'success' as const };
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1">
          Inventory Management
        </Typography>
        <Box>
          <Button
            variant="outlined"
            startIcon={<Refresh />}
            onClick={fetchInventory}
            sx={{ mr: 1 }}
          >
            Refresh
          </Button>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => setDialogOpen(true)}
          >
            Add Item
          </Button>
        </Box>
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

      <Stack direction="row" spacing={3} sx={{ mb: 3 }}>
        <Card sx={{ flex: 1 }}>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Total Items
            </Typography>
            <Typography variant="h4">
              {inventory.length}
            </Typography>
          </CardContent>
        </Card>
        <Card sx={{ flex: 1 }}>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Out of Stock
            </Typography>
            <Typography variant="h4" color="error">
              {inventory.filter(item => item.stock === 0).length}
            </Typography>
          </CardContent>
        </Card>
        <Card sx={{ flex: 1 }}>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Low Stock
            </Typography>
            <Typography variant="h4" color="warning.main">
              {inventory.filter(item => item.stock < item.capacity * 0.35 && item.stock > 0).length}
            </Typography>
          </CardContent>
        </Card>
        <Card sx={{ flex: 1 }}>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Overstocked
            </Typography>
            <Typography variant="h4" color="warning.main">
              {inventory.filter(item => item.stock > item.capacity).length}
            </Typography>
          </CardContent>
        </Card>
      </Stack>

      {/* Filter Buttons */}
      <Box sx={{ mb: 3 }}>
        <Typography variant="h6" sx={{ mb: 1 }}>
          Filter Inventory
        </Typography>
        <Stack direction="row" spacing={1}>
          <Button
            variant={filterView === 'all' ? 'contained' : 'outlined'}
            onClick={() => setFilterView('all')}
            startIcon={<CheckCircle />}
          >
            All Items
          </Button>
          <Button
            variant={filterView === 'out-of-stock' ? 'contained' : 'outlined'}
            onClick={() => setFilterView('out-of-stock')}
            startIcon={<Error />}
            color="error"
          >
            Out of Stock
          </Button>
          <Button
            variant={filterView === 'low-stock' ? 'contained' : 'outlined'}
            onClick={() => setFilterView('low-stock')}
            startIcon={<Warning />}
            color="warning"
          >
            Low Stock
          </Button>
          <Button
            variant={filterView === 'overstocked' ? 'contained' : 'outlined'}
            onClick={() => setFilterView('overstocked')}
            startIcon={<Warning />}
            color="warning"
          >
            Overstocked
          </Button>
        </Stack>
      </Box>

      {/* Search Item by ID */}
      <Box sx={{ mb: 3 }}>
        <Typography variant="h6" sx={{ mb: 1 }}>
          Search Item by ID
        </Typography>
        <Stack direction="row" spacing={2} alignItems="flex-start">
          <TextField
            label="Item ID"
            type="number"
            value={searchId}
            onChange={(e) => setSearchId(e.target.value)}
            size="small"
            sx={{ width: 200 }}
            onKeyPress={(e) => {
              if (e.key === 'Enter') {
                searchItemById();
              }
            }}
          />
          <Button
            variant="contained"
            startIcon={<Search />}
            onClick={searchItemById}
            disabled={!searchId.trim()}
          >
            Search
          </Button>
          <Button
            variant="outlined"
            startIcon={<Clear />}
            onClick={clearSearch}
            disabled={!searchResult && !searchId}
          >
            Clear
          </Button>
        </Stack>
        
        {/* Search Result */}
        {searchResult && (
          <Box sx={{ mt: 2 }}>
            <Card sx={{ maxWidth: 600 }}>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Item Details
                </Typography>
                <Stack spacing={1}>
                  <Typography><strong>ID:</strong> {searchResult.id}</Typography>
                  <Typography><strong>Name:</strong> {searchResult.name}</Typography>
                  <Typography><strong>Amount in Stock:</strong> {searchResult.stock}</Typography>
                  <Typography><strong>Total Capacity:</strong> {searchResult.capacity}</Typography>
                  <Box sx={{ mt: 1 }}>
                    <Chip
                      label={getStockStatus(searchResult).label}
                      color={getStockStatus(searchResult).color}
                      size="small"
                    />
                  </Box>
                </Stack>
              </CardContent>
            </Card>
          </Box>
        )}
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Item Name</TableCell>
              <TableCell align="right">Stock</TableCell>
              <TableCell align="right">Capacity</TableCell>
              <TableCell align="center">Status</TableCell>
              <TableCell align="center">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {inventory.map((item) => {
              const status = getStockStatus(item);
              return (
                <TableRow key={item.id}>
                  <TableCell>{item.id}</TableCell>
                  <TableCell>{item.name}</TableCell>
                  <TableCell align="right">{item.stock}</TableCell>
                  <TableCell align="right">{item.capacity}</TableCell>
                  <TableCell align="center">
                    <Chip
                      label={status.label}
                      color={status.color}
                      size="small"
                    />
                  </TableCell>
                  <TableCell align="center">
                    <Tooltip title="Edit">
                      <IconButton
                        size="small"
                        onClick={() => openEditDialog(item)}
                      >
                        <Edit />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Delete">
                      <IconButton
                        size="small"
                        onClick={() => handleDeleteItem(item.id)}
                        color="error"
                      >
                        <Delete />
                      </IconButton>
                    </Tooltip>
                  </TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Add Dialog */}
      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Add Inventory Item</DialogTitle>
        <DialogContent>
          <TextField
            select
            label="Item"
            value={formData.itemId}
            onChange={(e) => setFormData({ ...formData, itemId: e.target.value })}
            fullWidth
            margin="normal"
            SelectProps={{ native: true }}
          >
            <option value="">Select an item</option>
            {items.map((item) => (
              <option key={item.id} value={item.id}>
                {item.name}
              </option>
            ))}
          </TextField>
          <TextField
            label="Stock"
            type="number"
            value={formData.stock}
            onChange={(e) => setFormData({ ...formData, stock: e.target.value })}
            fullWidth
            margin="normal"
          />
          <TextField
            label="Capacity"
            type="number"
            value={formData.capacity}
            onChange={(e) => setFormData({ ...formData, capacity: e.target.value })}
            fullWidth
            margin="normal"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
          <Button
            onClick={handleAddItem}
            variant="contained"
            disabled={!formData.itemId || !formData.stock || !formData.capacity}
          >
            Add
          </Button>
        </DialogActions>
      </Dialog>

      {/* Edit Dialog */}
      <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Edit Inventory Item</DialogTitle>
        <DialogContent>
          <TextField
            label="Item Name"
            value={selectedItem?.name || ''}
            fullWidth
            margin="normal"
            disabled
          />
          <TextField
            label="Stock"
            type="number"
            value={formData.stock}
            onChange={(e) => setFormData({ ...formData, stock: e.target.value })}
            fullWidth
            margin="normal"
          />
          <TextField
            label="Capacity"
            type="number"
            value={formData.capacity}
            onChange={(e) => setFormData({ ...formData, capacity: e.target.value })}
            fullWidth
            margin="normal"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleUpdateItem} variant="contained">
            Update
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default InventoryManager;
