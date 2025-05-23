import { createContext, useEffect, useState, useMemo,useCallback } from "react";
import { assets } from "../assets/frontend_assets/assets";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import axios from "axios";

export const ShopContext = createContext();

const ShopContextProvider = (props) => {
    const currency = 'MAD';
    const delivery_fee = 20;
    const backendURL = import.meta.env.VITE_BACKEND_URL;
    const [search, setSearch] = useState('');
    const [showSearch, setShowSearch] = useState(false);
    const [cartItems, setCartItems] = useState({});
    const [products, setProducts] = useState([]);
    const navigate = useNavigate();

    const getCartCount = ()=>{
        let totalCount = 0;
        for (const items in cartItems){
            for (const item in cartItems[items]){
                try{
                    if(cartItems[items][item] > 0){
                        totalCount += cartItems[items][item];
                    }
                }catch (error){

                }

            }
        }
        return totalCount;
    }

    const addToCart = async(itemId,size)=>{

        if (!size){
            toast.error('Select Product Size');
            return;
        }

        let cartData = structuredClone(cartItems);

        if ( cartData[itemId]){
            if (cartData[itemId][size]){
                cartData[itemId][size] += 1;
            }
            else{
                cartData[itemId][size] = 1;
            }
        }
        else{
            cartData[itemId] = {};
            cartData[itemId][size] = 1;
        }
        setCartItems(cartData);

    }


    const updateQuantity = async(itemId,size,quantity)=> {
        let cartData = structuredClone(cartItems);;

        cartData[itemId][size] = quantity;

        setCartItems(cartData);
    }

    const getCartAmount = () => {
        let totalAmount  = 0;

        for(const items in cartItems){
            let itemInfo = products.find((product)=>product._id.toString() === items);
            for(const item in cartItems[items]){
                try{
                    if(cartItems[items][item] > 0){
                        totalAmount += cartItems[items][item] * itemInfo.price;
                    }
                }catch (error){
                }
            }
        }
        return totalAmount;
    }

    const getProductsData = async () => {
        try {
            const response = await axios.get(`${backendURL}/import`);
            const transformedProducts = response.data.map((product) => ({
                ...product,
                image: product.image?.map((imgName) => assets[imgName] || imgName) || [],
            }));
            setProducts(transformedProducts);
        } catch (error) {
            console.error("Erreur lors de la récupération des produits :", error);
            toast.error("Impossible de récupérer les produits.");
        }
    };

    useEffect(() => {
        getProductsData();
    }, []);

    const value = {
        products, currency, delivery_fee,
        search, setSearch, showSearch, setShowSearch,
        cartItems, addToCart,
        getCartCount, updateQuantity,
        getCartAmount, navigate, backendURL

    }
    return (
        <ShopContext.Provider value={value}>
            {props.children}
        </ShopContext.Provider>
    )
};

export default ShopContextProvider;
