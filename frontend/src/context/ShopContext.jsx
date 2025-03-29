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
    const productMap = useMemo(() => {
        return new Map(products.map(product => [product._id, product]));
    }, [products]);

    const getCartCount = () => {
        let totalCount = 0;
        for (const itemId in cartItems) {
            for (const size in cartItems[itemId]) {
                totalCount += cartItems[itemId][size];
            }
        }
        return totalCount;
    };

    const addToCart = (itemId, size) => {
        if (!size) {
            toast.error('Select Product Size');
            return;
        }

        const product = productMap.get(itemId);
        if (!product) {
            toast.error('Product not found');
            return;
        }

        if (!product.sizes?.includes(size)) {
            toast.error('Invalid size selection');
            return;
        }

        setCartItems(prev => ({
            ...prev,
            [itemId]: {
                ...prev[itemId],
                [size]: ((prev[itemId]?.[size] || 0) + 1)
            }
        }));
    };


    const updateQuantity = useCallback((itemId, size, quantity) => {
        setCartItems(prev => {
            const newItems = {...prev};
            const item = newItems[itemId];

            if (quantity < 1) {
                if (item) {
                    delete item[size];
                    if (Object.keys(item).length === 0) delete newItems[itemId];
                }
            } else {
                newItems[itemId] = {
                    ...item,
                    [size]: Math.min(quantity, 99)
                };
            }
            return newItems;
        });
    }, []);

    const getCartAmount = useCallback(() => {
        let totalAmount = 0;
        for (const [itemId, sizes] of Object.entries(cartItems)) {
            const product = productMap.get(itemId);
            if (!product) continue;

            for (const [size, quantity] of Object.entries(sizes)) {
                totalAmount += quantity * product.price;
            }
        }
        return Number(totalAmount.toFixed(2));
    }, [cartItems, productMap]);

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

    return (
        <ShopContext.Provider value={{
            products, currency, delivery_fee, search, setSearch, showSearch, setShowSearch,
            cartItems, addToCart, updateQuantity, getCartAmount, navigate, backendURL,getCartCount
        }}>
            {props.children}
        </ShopContext.Provider>
    );
};

export default ShopContextProvider;
