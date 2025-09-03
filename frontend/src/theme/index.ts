import { createTheme } from '@mui/material/styles';

// Export the theme creation function for use in App.tsx
export const getTheme = (darkMode: boolean) => createTheme({
  palette: {
    mode: darkMode ? 'dark' : 'light',
    primary: {
      main: darkMode ? '#90caf9' : '#1976d2',
    },
    secondary: {
      main: darkMode ? '#f48fb1' : '#dc004e',
    },
    background: {
      default: darkMode ? '#121212' : '#f5f5f5',
      paper: darkMode ? '#1e1e1e' : '#ffffff',
    },
    text: {
      primary: darkMode ? '#ffffff' : '#000000',
      secondary: darkMode ? '#b3b3b3' : '#666666',
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
  components: {
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundColor: darkMode ? '#2d2d2d' : '#ffffff',
          boxShadow: darkMode 
            ? '0px 2px 1px -1px rgba(255,255,255,0.2), 0px 1px 1px 0px rgba(255,255,255,0.14), 0px 1px 3px 0px rgba(255,255,255,0.12)'
            : '0px 2px 1px -1px rgba(0,0,0,0.2), 0px 1px 1px 0px rgba(0,0,0,0.14), 0px 1px 3px 0px rgba(0,0,0,0.12)',
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          '&.MuiChip-colorSuccess': {
            backgroundColor: darkMode ? '#2e7d32' : '#4caf50',
            color: '#ffffff',
          },
          '&.MuiChip-colorWarning': {
            backgroundColor: darkMode ? '#ed6c02' : '#ff9800',
            color: '#ffffff',
          },
          '&.MuiChip-colorError': {
            backgroundColor: darkMode ? '#d32f2f' : '#f44336',
            color: '#ffffff',
          },
        },
      },
    },
    MuiTableContainer: {
      styleOverrides: {
        root: {
          backgroundColor: darkMode ? '#2d2d2d' : '#ffffff',
        },
      },
    },
    MuiAlert: {
      styleOverrides: {
        root: {
          '&.MuiAlert-standardSuccess': {
            backgroundColor: darkMode ? '#1b5e20' : '#e8f5e8',
            color: darkMode ? '#ffffff' : '#2e7d32',
          },
          '&.MuiAlert-standardError': {
            backgroundColor: darkMode ? '#b71c1c' : '#ffebee',
            color: darkMode ? '#ffffff' : '#c62828',
          },
        },
      },
    },
  },
});
