import React, { useState, useEffect } from 'react';
import {
  Container,
  CssBaseline,
  ThemeProvider,
  AppBar,
  Toolbar,
  Typography,
  Box,
  Tabs,
  Tab,
  IconButton,
  Tooltip,
} from '@mui/material';
import { 
  Inventory, 
  Store, 
  TrendingUp, 
  ImportExport, 
  DarkMode, 
  LightMode 
} from '@mui/icons-material';
import {
  InventoryManager,
  DistributorManager,
  ItemManager,
  ExportManager,
} from './components';
import { ThemeContext } from './contexts/ThemeContext';
import { getTheme } from './theme';

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
  const [darkMode, setDarkMode] = useState(() => {
    // Check localStorage for saved preference
    const saved = localStorage.getItem('darkMode');
    return saved ? JSON.parse(saved) : false;
  });

  // Save dark mode preference to localStorage
  useEffect(() => {
    localStorage.setItem('darkMode', JSON.stringify(darkMode));
  }, [darkMode]);

  const toggleDarkMode = () => {
    setDarkMode((prev: boolean) => !prev);
  };

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  const theme = getTheme(darkMode);

  return (
    <ThemeContext.Provider value={{ darkMode, toggleDarkMode }}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <AppBar position="static" elevation={1}>
          <Toolbar>
            <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
              ECP Management System
            </Typography>
            <Tooltip title={darkMode ? 'Switch to Light Mode' : 'Switch to Dark Mode'}>
              <IconButton 
                color="inherit" 
                onClick={toggleDarkMode}
                sx={{ ml: 1 }}
              >
                {darkMode ? <LightMode /> : <DarkMode />}
              </IconButton>
            </Tooltip>
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
    </ThemeContext.Provider>
  );
}

export default App;
