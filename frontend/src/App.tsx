import React, { useState } from 'react';
import {
  Container,
  CssBaseline,
  ThemeProvider,
  createTheme,
  AppBar,
  Toolbar,
  Typography,
  Box,
  Tabs,
  Tab,
} from '@mui/material';
import { Inventory, Store, TrendingUp, ImportExport } from '@mui/icons-material';
import {
  InventoryManager,
  DistributorManager,
  ItemManager,
  ExportManager,
} from './components';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
    background: {
      default: '#f5f5f5',
    },
  },
  typography: {
    h4: {
      fontWeight: 600,
    },
    h6: {
      fontWeight: 500,
    },
  },
});

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

function App() {
  const [tabValue, setTabValue] = useState(0);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AppBar position="static" elevation={1}>
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            TopBloc Inventory Management System
          </Typography>
        </Toolbar>
      </AppBar>
      
      <Container maxWidth="lg" sx={{ mt: 2 }}>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tabs value={tabValue} onChange={handleTabChange} aria-label="main navigation">
            <Tab 
              icon={<Inventory />} 
              label="Inventory" 
              iconPosition="start"
              sx={{ minHeight: 48 }}
            />
            <Tab 
              icon={<Store />} 
              label="Distributors" 
              iconPosition="start"
              sx={{ minHeight: 48 }}
            />
            <Tab 
              icon={<TrendingUp />} 
              label="Items" 
              iconPosition="start"
              sx={{ minHeight: 48 }}
            />
            <Tab 
              icon={<ImportExport />} 
              label="Export" 
              iconPosition="start"
              sx={{ minHeight: 48 }}
            />
          </Tabs>
        </Box>

        <TabPanel value={tabValue} index={0}>
          <InventoryManager />
        </TabPanel>
        <TabPanel value={tabValue} index={1}>
          <DistributorManager />
        </TabPanel>
        <TabPanel value={tabValue} index={2}>
          <ItemManager />
        </TabPanel>
        <TabPanel value={tabValue} index={3}>
          <ExportManager />
        </TabPanel>
      </Container>
    </ThemeProvider>
  );
}

export default App;
