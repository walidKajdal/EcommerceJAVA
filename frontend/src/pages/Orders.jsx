import React, { useContext } from 'react';
import { ShopContext } from '../context/ShopContext';
import { useNavigate } from 'react-router-dom';

const Orders = () => {
    const {
        products = [],
        currency,
        cartItems = {},
        updateQuantity
    } = useContext(ShopContext);

    const navigate = useNavigate();

    // Safely transform cartIt
    const cartData = [];
    if (cartItems) {
        for (const itemId in cartItems) {
            for (const size in cartItems[itemId]) {
                if (cartItems[itemId][size] > 0) {
                    cartData.push({
                        _id: itemId,
                        size: size,
                        quantity: cartItems[itemId][size]
                    });
                }
            }
        }
    }

    // Check if products is loaded
    if (!products || products.length === 0) {
        return <div className="border-t pt-16">Loading products...</div>;
    }

    return (
        <div className='border-t pt-16'>
            <div className='text-2xl'>
                <h1 className="text-center font-bold">MY ORDERS</h1>
            </div>

            <div className='border-t pt-14'>
                {cartData.length === 0 ? (
                    <div className="py-8 text-center">
                        <p>Your cart is empty</p>
                    </div>
                ) : (
                    <>
                        <div>
                            {cartData.map((item, index) => {
                                const productData = products.find(
                                    (product) => product?._id?.toString() === item?._id?.toString()
                                );

                                if (!productData) return null;

                                return (
                                    <div key={index} className='py-4 border-t border-b text-gray-700 flex flex-col md:flex-row md:items-center md:justify-between gap-4'>
                                        <div className='flex items-start gap-6 text-sm'>
                                            {productData?.image?.[0] && (
                                                <img className='w-16 sm:w-20'
                                                     src={productData.image[0]}
                                                     alt={productData?.name || 'Product'}
                                                />
                                            )}
                                            <div>
                                                <p className='sm:text-base font-medium'>
                                                    {productData?.name || 'Unknown Product'}
                                                </p>
                                                <div className='flex items-center gap-3 mt-2 text-base text-gray-700'>
                                                    <p className='text-lg'>{currency}{productData?.price || 0}</p>
                                                    <p>Quantity: {item.quantity}</p>
                                                    <p>Size: {item.size}</p>
                                                </div>
                                                <p className='mt-2'>
                                                    Date: <span className='text-gray-400'>
                                                        {new Date().toLocaleDateString('en-US', {
                                                            day: 'numeric',
                                                            month: 'short'
                                                        })}
                                                    </span>
                                                </p>
                                            </div>
                                        </div>
                                        <div className='md:w-1/2 flex justify-between'>
                                            <div className='flex items-center gap-2'>
                                                <p className='min-w-2 h-2 rounded-full bg-green-500'></p>
                                                <p className='text-sm md:text-base'>Ready to ship</p>
                                            </div>
                                            <button
                                                onClick={() => navigate(`/tracking/${item._id}/`)}
                                                className='border px-4 py-2 text-sm font-medium rounded-sm hover:bg-gray-100'
                                            >
                                                Track Order
                                            </button>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                        <div className='flex justify-center my-20'>
                            <div className='w-full sm:w-[450px]'>
                                <div className='w-full text-center'>
                                    <button
                                        onClick={() => navigate('/collection')}
                                        className='bg-black text-white text-sm my-8 px-8 py-3'
                                    >
                                        BACK TO SHOP
                                    </button>
                                </div>
                            </div>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
};

export default Orders;