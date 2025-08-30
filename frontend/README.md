# TopBloc Inventory Management System - Frontend

A modern React TypeScript application for managing inventory, distributors, and items with a clean Material-UI interface.

## Features

### 🏪 Inventory Management
- View all inventory items with stock levels and capacity
- Real-time status indicators (Out of Stock, Low Stock, Overstocked)
- Add new inventory items
- Update stock and capacity levels
- Delete inventory items
- Dashboard with key metrics

### 🚛 Distributor Management
- Manage distributors and their catalogs
- View distributor-specific item pricing
- Add new distributors
- Add items to distributor catalogs with pricing
- Interactive selection to view distributor details

### 📦 Item Management
- Manage all items in the system
- View which distributors carry each item
- Find cheapest restock options for specific quantities
- Add new items to the system

### 📊 Export & Utilities
- Export any table to CSV format
- Download data for items, inventory, distributors, and prices
- Custom table export functionality
- Database reset utility

## Technology Stack

- **React 18** with TypeScript
- **Material-UI (MUI)** for modern, responsive design
- **Axios** for API communication
- **CSS3** with custom styling

## Getting Started

### Prerequisites
- Node.js 14+ and npm
- Backend API running on `http://localhost:4567`

### Installation
```bash
cd frontend
npm install
npm start
```

The application will open at `http://localhost:3000`

## Available Scripts

In the project directory, you can run:

### `npm start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

The page will reload if you make edits.\
You will also see any lint errors in the console.

### `npm test`

Launches the test runner in the interactive watch mode.\
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `npm run build`

Builds the app for production to the `build` folder.\
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.\
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

### `npm run eject`

**Note: this is a one-way operation. Once you `eject`, you can’t go back!**

If you aren’t satisfied with the build tool and configuration choices, you can `eject` at any time. This command will remove the single build dependency from your project.

Instead, it will copy all the configuration files and the transitive dependencies (webpack, Babel, ESLint, etc) right into your project so you have full control over them. All of the commands except `eject` will still work, but they will point to the copied scripts so you can tweak them. At this point you’re on your own.

You don’t have to ever use `eject`. The curated feature set is suitable for small and middle deployments, and you shouldn’t feel obligated to use this feature. However we understand that this tool wouldn’t be useful if you couldn’t customize it when you are ready for it.

## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).
