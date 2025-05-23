import React, { useContext, useState, useEffect } from 'react';
import { assets } from '../assets/frontend_assets/assets';
import { NavLink, Link, useNavigate } from 'react-router-dom';
import { ShopContext } from '../context/ShopContext';

const Navbar = () => {
  const [visible, setVisible] = useState(false);
  const { setShowSearch, getCartCount } = useContext(ShopContext);
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem("jwt"));
  const navigate = useNavigate();

  useEffect(() => {
    const handleStorageChange = () => {
      setIsLoggedIn(!!localStorage.getItem("jwt"));
    };

    window.addEventListener("storage", handleStorageChange);
    return () => window.removeEventListener("storage", handleStorageChange);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("jwt");
    setIsLoggedIn(false);
    window.dispatchEvent(new Event("storage"));
    navigate("/login");
  };

  return (
      <div className='flex items-center justify-between py-5 font-medium'>
        <Link to='/home'><img src={assets.logo} alt='logo' className='w-40' /></Link>

        <ul className='hidden sm:flex gap-5 text-sm text-gray-700'>
          <NavLink to='/home' className='flex flex-col items-center gap-1'> <p>HOME</p> </NavLink>
          <NavLink to='/collection' className='flex flex-col items-center gap-1'> <p>COLLECTION</p> </NavLink>
          <NavLink to='/about' className='flex flex-col items-center gap-1'> <p>ABOUT</p> </NavLink>
          <NavLink to='/contact' className='flex flex-col items-center gap-1'> <p>CONTACT</p> </NavLink>
        </ul>

        <div className='flex items-center gap-6'>
          <img onClick={() => setShowSearch(true)} src={assets.search_icon} className='w-5 cursor-pointer' alt="" />

          {isLoggedIn ? (
              <div className='group relative'>
                <img className='w-5 cursor-pointer' src={assets.profile_icon} alt="Profile" />
                <div className='absolute right-0 pt-4 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-opacity duration-300 dropdown-menu'>
                  <div className='flex flex-col gap-2 w-36 py-3 px-5 bg-slate-100 text-gray-500 rounded'>
                    <Link to="/profile" className='cursor-pointer hover:text-black'>My Profile</Link>
                    <Link to="/orders" className='cursor-pointer hover:text-black'>Orders</Link>
                    <p onClick={handleLogout} className='cursor-pointer hover:text-black'>Logout</p>
                  </div>
                </div>
              </div>
          ) : (
              <div className="flex items-center gap-4">
                <Link to="/login" className="bg-black text-white px-4 py-2 rounded text-sm">Login</Link>
                <Link to="/login?signup=true" className="bg-gray-700 text-white px-4 py-2 rounded text-sm">Sign Up</Link>
              </div>
          )}

          <Link to='/cart' className='relative'>
            <img src={assets.cart_icon} className='w-5 min-w-5' alt="Cart" />
            <p className='absolute right-[-5px] bottom-[-5px] w-4 text-center leading-4 bg-black text-white aspect-square rounded-full text-[8px]'>{getCartCount()}</p>
          </Link>

          <img onClick={() => setVisible(true)} src={assets.menu_icon} className='w-5 cursor-pointer sm:hidden' alt="Menu" />
        </div>

        <div className={`absolute top-0 bottom-0 overflow-hidden bg-white transition-all ${visible ? 'w-full' : 'w-0'}`}>
          <div className='flex flex-col text-gray-600'>
            <div onClick={() => setVisible(false)} className='flex items-center gap-4 p-3 cursor-pointer'>
              <img className='h4 rotate-180' src={assets.dropdown_icon} alt="Back" />
              <p>Back</p>
            </div>
            <NavLink onClick={() => setVisible(false)} className='py-2 pl-6 border' to='/home'>HOME</NavLink>
            <NavLink onClick={() => setVisible(false)} className='py-2 pl-6 border' to='/collection'>COLLECTION</NavLink>
            <NavLink onClick={() => setVisible(false)} className='py-2 pl-6 border' to='/about'>ABOUT</NavLink>
            <NavLink onClick={() => setVisible(false)} className='py-2 pl-6 border' to='/contact'>CONTACT</NavLink>
          </div>
        </div>
      </div>
  );
};

export default Navbar;
