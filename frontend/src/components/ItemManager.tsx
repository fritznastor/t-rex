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
  Alert,
  Card,
  CardContent,
  Stack,
} from '@mui/material';
import {
  Add,
  Refresh,
  ShoppingCart,
  Search,
} from '@mui/icons-material';
import { apiService, Item, DistributorPrice } from '../services/api';

interface ItemFormData {
  name: string;
}

interface CheapestFormData {
  itemId: string;
  quantity: string;
}

const ItemManager: React.FC = () => {
  const [items, setItems] = useState<Item[]>([]);
  const [itemDistributors, setItemDistributors] = useState<DistributorPrice[]>([]);
  const [cheapestResult, setCheapestResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [itemDialogOpen, setItemDialogOpen] = useState(false);
  const [cheapestDialogOpen, setCheapestDialogOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState<number | null>(null);
  const [itemFormData, setItemFormData] = useState<ItemFormData>({
    name: '',
  });
  const [cheapestFormData, setCheapestFormData] = useState<CheapestFormData>({
    itemId: '',
    quantity: '',
  });

  useEffect(() => {
    fetchItems();
  }, []);

  useEffect(() => {
    if (selectedItem) {
      fetchItemDistributors(selectedItem);
    }
  }, [selectedItem]);

  const fetchItems = async () => {
    try {
      const response = await apiService.getItems();
      setItems(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch items');
    }
  };

  const fetchItemDistributors = async (itemId: number) => {
    try {
      const response = await apiService.getItemDistributors(itemId);
      setItemDistributors(response.data);
    } catch (err) {
      console.error('Failed to fetch item distributors');
    }
  };

  const handleAddItem = async () => {
    try {
      await apiService.addItem(itemFormData.name);
      setSuccess('Item added successfully!');
      setItemDialogOpen(false);
      setItemFormData({ name: '' });
      fetchItems();
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to add item');
    }
  };

  const handleFindCheapest = async () => {
    try {
      const response = await apiService.getCheapestRestock(
        parseInt(cheapestFormData.itemId),
        parseInt(cheapestFormData.quantity)
      );
      setCheapestResult(response.data);
      setSuccess('Found cheapest restock option!');
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to find cheapest restock');
    }
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1">
          Item Management
        </Typography>
        <Box>
          <Button
            variant="outlined"
            startIcon={<Search />}
            onClick={() => setCheapestDialogOpen(true)}
            sx={{ mr: 1 }}
          >
            Find Cheapest
          </Button>
          <Button
            variant="outlined"
            startIcon={<Refresh />}
            onClick={fetchItems}
            sx={{ mr: 1 }}
          >
            Refresh
          </Button>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => setItemDialogOpen(true)}
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
              {items.length}
            </Typography>
          </CardContent>
        </Card>
        <Card sx={{ flex: 1 }}>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Available Distributors
            </Typography>
            <Typography variant="h4">
              {itemDistributors.length}
            </Typography>
          </CardContent>
        </Card>
      </Stack>

      <Stack direction="row" spacing={3}>
        {/* Items Table */}
        <Box sx={{ flex: 1 }}>
          <Typography variant="h6" sx={{ mb: 2 }}>
            Items
          </Typography>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Name</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {items.map((item) => (
                  <TableRow 
                    key={item.id}
                    selected={selectedItem === item.id}
                    onClick={() => setSelectedItem(item.id)}
                    sx={{ cursor: 'pointer' }}
                  >
                    <TableCell>{item.id}</TableCell>
                    <TableCell>{item.name}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Box>

        {/* Item Distributors Table */}
        <Box sx={{ flex: 1 }}>
          <Typography variant="h6" sx={{ mb: 2 }}>
            {selectedItem 
              ? `Distributors for ${items.find(i => i.id === selectedItem)?.name}`
              : 'Select an item'
            }
          </Typography>
          
          {selectedItem ? (
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Distributor</TableCell>
                    <TableCell align="right">Cost</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {itemDistributors.map((distributor, index) => (
                    <TableRow key={index}>
                      <TableCell>{distributor.distributor_name}</TableCell>
                      <TableCell align="right">${distributor.cost.toFixed(2)}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          ) : (
            <Paper sx={{ p: 3, textAlign: 'center' }}>
              <ShoppingCart sx={{ fontSize: 60, color: 'text.secondary', mb: 2 }} />
              <Typography color="textSecondary">
                Select an item to view its distributors and prices
              </Typography>
            </Paper>
          )}
        </Box>
      </Stack>

      {/* Cheapest Result Card */}
      {cheapestResult && (
        <Card sx={{ mt: 3 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Cheapest Restock Option
            </Typography>
            <Typography>
              <strong>Item:</strong> {cheapestResult.item_name || cheapestResult.itemName}
            </Typography>
            <Typography>
              <strong>Quantity:</strong> {cheapestResult.quantity}
            </Typography>
            <Typography>
              <strong>Best Distributor:</strong> {cheapestResult.distributor_name || cheapestResult.distributorName}
            </Typography>
            <Typography>
              <strong>Unit Cost:</strong> ${cheapestResult.unit_cost || cheapestResult.unitCost}
            </Typography>
            <Typography>
              <strong>Total Cost:</strong> ${cheapestResult.total_cost || cheapestResult.totalCost}
            </Typography>
          </CardContent>
        </Card>
      )}

      {/* Add Item Dialog */}
      <Dialog open={itemDialogOpen} onClose={() => setItemDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Add Item</DialogTitle>
        <DialogContent>
          <TextField
            label="Item Name"
            value={itemFormData.name}
            onChange={(e) => setItemFormData({ name: e.target.value })}
            fullWidth
            margin="normal"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setItemDialogOpen(false)}>Cancel</Button>
          <Button
            onClick={handleAddItem}
            variant="contained"
            disabled={!itemFormData.name.trim()}
          >
            Add
          </Button>
        </DialogActions>
      </Dialog>

      {/* Find Cheapest Dialog */}
      <Dialog open={cheapestDialogOpen} onClose={() => setCheapestDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Find Cheapest Restock</DialogTitle>
        <DialogContent>
          <TextField
            select
            label="Item"
            value={cheapestFormData.itemId}
            onChange={(e) => setCheapestFormData({ ...cheapestFormData, itemId: e.target.value })}
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
            label="Quantity"
            type="number"
            value={cheapestFormData.quantity}
            onChange={(e) => setCheapestFormData({ ...cheapestFormData, quantity: e.target.value })}
            fullWidth
            margin="normal"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCheapestDialogOpen(false)}>Cancel</Button>
          <Button
            onClick={handleFindCheapest}
            variant="contained"
            disabled={!cheapestFormData.itemId || !cheapestFormData.quantity}
          >
            Find Cheapest
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default ItemManager;
