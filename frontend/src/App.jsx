import './App.css'
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Landing from "./pages/Landing.jsx";
import Dashboard from "./pages/Dashboard.jsx";
import Categories from "./pages/Categories.jsx";
import Budgets from "./pages/Budgets.jsx";
import Transactions from "./pages/Transactions.jsx";
import Register from "./pages/Register.jsx";
import Login from "./pages/Login.jsx";
import PrivateRouter from "./components/PrivateRouter.jsx";
import NotFoundRedirect from "./components/NotFoundRedirect.jsx";
import Layout from "./components/Layout.jsx";

function App() {

  return (

      <BrowserRouter>
          <Routes>
              <Route path="/" element={<PrivateRouter publicPage> <Landing /> </PrivateRouter>} />
              <Route path="/register" element={<PrivateRouter publicPage> <Register /> </PrivateRouter>} />
              <Route path="/login" element={<PrivateRouter publicPage> <Login /> </PrivateRouter>} />
              <Route
                  element={
                    <PrivateRouter>
                        <Layout/>
                    </PrivateRouter>
                  }
              >
                  <Route path="/dashboard" element={<Dashboard />} />
                  <Route path="/categories" element={<Categories />} />
                  <Route path="/budgets" element={<Budgets />}/>
                  <Route path="/transactions" element={<Transactions />} />

              </Route>

              <Route path="*" element={<NotFoundRedirect />} />
          </Routes>
      </BrowserRouter>

  )
}

export default App
