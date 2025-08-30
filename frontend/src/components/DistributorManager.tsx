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
  IconButton,
  Tooltip,
  Stack,
} from '@mui/material';
import {
  Add,
  Delete,
  Refresh,
  Store,
  Edit,
} from '@mui/icons-material';
import { apiService, Distributor, DistributorPrice, Item } from '../services/api';

interface DistributorFormData {
  name: string;
}

interface PriceFormData {
  distributorId: string;
  itemId: string;
  cost: string;
}

const DistributorManager: React.FC = () => {
  const [distributors, setDistributors] = useState<Distributor[]>([]);
  const [distributorPrices, setDistributorPrices] = useState<DistributorPrice[]>([]);
  const [items, setItems] = useState<Item[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [distributorDialogOpen, setDistributorDialogOpen] = useState(false);
  const [priceDialogOpen, setPriceDialogOpen] = useState(false);
  const [editPriceDialogOpen, setEditPriceDialogOpen] = useState(false);
  const [selectedDistributor, setSelectedDistributor] = useState<number | null>(null);
  const [selectedPrice, setSelectedPrice] = useState<DistributorPrice | null>(null);
  const [distributorFormData, setDistributorFormData] = useState<DistributorFormData>({
    name: '',
  });
  const [priceFormData, setPriceFormData] = useState<PriceFormData>({
    distributorId: '',
    itemId: '',
    cost: '',
  });

  useEffect(() => {
    fetchDistributors();
    fetchItems();
  }, []);

  useEffect(() => {
    if (selectedDistributor) {
      fetchDistributorPrices(selectedDistributor);
    }
  }, [selectedDistributor]);

  const fetchDistributors = async () => {
    try {
      const response = await apiService.getDistributors();
      setDistributors(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch distributors');
    }
  };

  const fetchDistributorPrices = async (distributorId: number) => {
    try {
      const response = await apiService.getDistributorItems(distributorId);
      setDistributorPrices(response.data);
    } catch (err) {
      console.error('Failed to fetch distributor prices');
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

  const handleAddDistributor = async () => {
    try {
      await apiService.addDistributor(distributorFormData.name);
      setSuccess('Distributor added successfully!');
      setDistributorDialogOpen(false);
      setDistributorFormData({ name: '' });
      fetchDistributors();
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to add distributor');
    }
  };

  const handleAddPrice = async () => {
    try {
      await apiService.addDistributorPrice(
        parseInt(priceFormData.distributorId),
        parseInt(priceFormData.itemId),
        parseFloat(priceFormData.cost)
      );
      setSuccess('Price added successfully!');
      setPriceDialogOpen(false);
      setPriceFormData({ distributorId: '', itemId: '', cost: '' });
      if (selectedDistributor) {
        fetchDistributorPrices(selectedDistributor);
      }
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to add price');
    }
  };

  const handleUpdatePrice = async () => {
    if (!selectedPrice) return;
    try {
      await apiService.updateDistributorPrice(
        selectedPrice.distributor_id,
        selectedPrice.item_id,
        parseFloat(priceFormData.cost)
      );
      setSuccess('Price updated successfully!');
      setEditPriceDialogOpen(false);
      setSelectedPrice(null);
      setPriceFormData({ distributorId: '', itemId: '', cost: '' });
      if (selectedDistributor) {
        fetchDistributorPrices(selectedDistributor);
      }
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to update price');
    }
  };

  const openEditPriceDialog = (price: DistributorPrice) => {
    setSelectedPrice(price);
    setPriceFormData({
      distributorId: price.distributor_id.toString(),
      itemId: price.item_id.toString(),
      cost: price.cost.toString(),
    });
    setEditPriceDialogOpen(true);
  };

  const handleDeleteDistributor = async (id: number) => {
    try {
      await apiService.deleteDistributor(id);
      setSuccess('Distributor deleted successfully!');
      fetchDistributors();
      if (selectedDistributor === id) {
        setSelectedDistributor(null);
        setDistributorPrices([]);
      }
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to delete distributor');
    }
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1">
          Distributor Management
        </Typography>
        <Box>
          <Button
            variant="outlined"
            startIcon={<Refresh />}
            onClick={fetchDistributors}
            sx={{ mr: 1 }}
          >
            Refresh
          </Button>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => setDistributorDialogOpen(true)}
          >
            Add Distributor
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
              Total Distributors
            </Typography>
            <Typography variant="h4">
              {distributors.length}
            </Typography>
          </CardContent>
        </Card>
        <Card sx={{ flex: 1 }}>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Total Price Entries
            </Typography>
            <Typography variant="h4">
              {distributorPrices.length}
            </Typography>
          </CardContent>
        </Card>
      </Stack>

      <Stack direction="row" spacing={3}>
        {/* Distributors Table */}
        <Box sx={{ flex: 1 }}>
          <Typography variant="h6" sx={{ mb: 2 }}>
            Distributors
          </Typography>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Name</TableCell>
                  <TableCell align="center">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {distributors.map((distributor) => (
                  <TableRow 
                    key={distributor.id}
                    selected={selectedDistributor === distributor.id}
                    onClick={() => setSelectedDistributor(distributor.id)}
                    sx={{ cursor: 'pointer' }}
                  >
                    <TableCell>{distributor.id}</TableCell>
                    <TableCell>{distributor.name}</TableCell>
                    <TableCell align="center">
                      <Tooltip title="Delete">
                        <IconButton
                          size="small"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleDeleteDistributor(distributor.id);
                          }}
                          color="error"
                        >
                          <Delete />
                        </IconButton>
                      </Tooltip>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Box>

        {/* Distributor Items Table */}
        <Box sx={{ flex: 1 }}>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
            <Typography variant="h6">
              {selectedDistributor 
                ? `Items for ${distributors.find(d => d.id === selectedDistributor)?.name}`
                : 'Select a distributor'
              }
            </Typography>
            {selectedDistributor && (
              <Button
                variant="outlined"
                size="small"
                startIcon={<Add />}
                onClick={() => {
                  setPriceFormData({ 
                    ...priceFormData, 
                    distributorId: selectedDistributor.toString() 
                  });
                  setPriceDialogOpen(true);
                }}
              >
                Add Item
              </Button>
            )}
          </Box>
          
          {selectedDistributor ? (
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Item Name</TableCell>
                    <TableCell align="right">Cost</TableCell>
                    <TableCell align="center">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {distributorPrices.map((price, index) => (
                    <TableRow key={index}>
                      <TableCell>{price.item_name}</TableCell>
                      <TableCell align="right">${price.cost.toFixed(2)}</TableCell>
                      <TableCell align="center">
                        <Tooltip title="Edit Price">
                          <IconButton
                            size="small"
                            onClick={() => openEditPriceDialog(price)}
                          >
                            <Edit />
                          </IconButton>
                        </Tooltip>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          ) : (
            <Paper sx={{ p: 3, textAlign: 'center' }}>
              <Store sx={{ fontSize: 60, color: 'text.secondary', mb: 2 }} />
              <Typography color="textSecondary">
                Select a distributor to view their items and prices
              </Typography>
            </Paper>
          )}
        </Box>
      </Stack>

      {/* Add Distributor Dialog */}
      <Dialog open={distributorDialogOpen} onClose={() => setDistributorDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Add Distributor</DialogTitle>
        <DialogContent>
          <TextField
            label="Distributor Name"
            value={distributorFormData.name}
            onChange={(e) => setDistributorFormData({ name: e.target.value })}
            fullWidth
            margin="normal"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDistributorDialogOpen(false)}>Cancel</Button>
          <Button
            onClick={handleAddDistributor}
            variant="contained"
            disabled={!distributorFormData.name.trim()}
          >
            Add
          </Button>
        </DialogActions>
      </Dialog>

      {/* Add Price Dialog */}
      <Dialog open={priceDialogOpen} onClose={() => setPriceDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Add Item Price</DialogTitle>
        <DialogContent>
          <TextField
            label="Distributor"
            value={distributors.find(d => d.id === parseInt(priceFormData.distributorId))?.name || ''}
            fullWidth
            margin="normal"
            disabled
          />
          <TextField
            select
            label="Item"
            value={priceFormData.itemId}
            onChange={(e) => setPriceFormData({ ...priceFormData, itemId: e.target.value })}
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
            label="Cost"
            type="number"
            value={priceFormData.cost}
            onChange={(e) => setPriceFormData({ ...priceFormData, cost: e.target.value })}
            fullWidth
            margin="normal"
            inputProps={{ step: 0.01 }}
            InputProps={{
              startAdornment: '$',
            }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPriceDialogOpen(false)}>Cancel</Button>
          <Button
            onClick={handleAddPrice}
            variant="contained"
            disabled={!priceFormData.itemId || !priceFormData.cost}
          >
            Add
          </Button>
        </DialogActions>
      </Dialog>

      {/* Edit Price Dialog */}
      <Dialog open={editPriceDialogOpen} onClose={() => setEditPriceDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Edit Item Price</DialogTitle>
        <DialogContent>
          <TextField
            label="Distributor"
            value={distributors.find(d => d.id === selectedPrice?.distributor_id)?.name || ''}
            fullWidth
            margin="normal"
            disabled
          />
          <TextField
            label="Item"
            value={selectedPrice?.item_name || ''}
            fullWidth
            margin="normal"
            disabled
          />
          <TextField
            label="Cost"
            type="number"
            value={priceFormData.cost}
            onChange={(e) => setPriceFormData({ ...priceFormData, cost: e.target.value })}
            fullWidth
            margin="normal"
            inputProps={{ step: 0.01 }}
            InputProps={{
              startAdornment: '$',
            }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditPriceDialogOpen(false)}>Cancel</Button>
          <Button
            onClick={handleUpdatePrice}
            variant="contained"
            disabled={!priceFormData.cost}
          >
            Update
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default DistributorManager;
