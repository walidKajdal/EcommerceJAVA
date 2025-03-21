import React, { useContext, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { ShopContext } from '../context/ShopContext';
import { assets } from '../assets/frontend_assets/assets';
import RelatedProducts from '../components/RelatedProducts';

const Product = () => {
  const { productId } = useParams();
  const { products, currency, addToCart, backendURL } = useContext(ShopContext);
  const [productData, setProductData] = useState(null);
  const [selectedImage, setSelectedImage] = useState('');
  const [selectedSize, setSelectedSize] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        // Vérifier d'abord si le produit est déjà dans le contexte
        const contextProduct = products.find(item => item._id === productId);

        if (contextProduct) {
          setProductData(contextProduct);
          setSelectedImage(`${backendURL}/${contextProduct.image[0]}`);
        } else {
          const response = await fetch(`${backendURL}/import/${productId}`);
          if (!response.ok) throw new Error('Product not found');

          const data = await response.json();
          setProductData(data);
          setSelectedImage(`${backendURL}/${data.image[0]}`);
        }
      } catch (err) {
        setError(err.message);
        console.error("Error loading product:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [productId, products, backendURL]);

  if (loading) {
    return <div className="text-center py-8">Loading product...</div>;
  }

  if (error) {
    return <div className="text-center py-8 text-red-500">Error: {error}</div>;
  }

  if (!productData) {
    return <div className="text-center py-8">Product not found</div>;
  }

  return (
      <div className='border-t-2 pt-10'>
        {/* Product Data */}
        <div className='flex flex-col sm:flex-row gap-8'>

          {/* Product Images */}
          <div className='flex-1 flex flex-col-reverse gap-3 sm:flex-row'>
            <div className='flex sm:flex-col overflow-x-auto sm:overflow-y-auto sm:w-1/5 gap-2'>
              {productData.image?.map((img, index) => (
                  <img
                      key={index}
                      onClick={() => setSelectedImage(`${backendURL}/${img}`)}
                      src={`${backendURL}/${img}`}
                      className={`w-24 h-24 object-cover cursor-pointer border ${
                          img === selectedImage ? 'border-orange-500' : 'border-gray-200'
                      }`}
                      alt={`Product view ${index + 1}`}
                  />
              ))}
            </div>
            <div className='w-full sm:w-4/5'>
              <img
                  src={selectedImage}
                  className='w-full h-auto max-h-96 object-contain'
                  alt="Main product"
              />
            </div>
          </div>

          {/* Product Details */}
          <div className='flex-1 space-y-4'>
            <h1 className='text-3xl font-semibold'>{productData.name}</h1>

            <div className='flex items-center gap-1'>
              {[...Array(4)].map((_, i) => (
                  <img key={i} src={assets.star_icon} alt="star" className="w-4" />
              ))}
              <img src={assets.star_dull_icon} alt="star" className="w-4" />
              <span className='ml-2 text-gray-600'>(122 reviews)</span>
            </div>

            <p className='text-4xl font-bold'>{currency}{productData.price}</p>
            <p className='text-gray-600'>{productData.description}</p>

            <div className='space-y-4'>
              <p className='font-medium'>Select Size</p>
              <div className='flex flex-wrap gap-2'>
                {productData.sizes?.map((size, index) => (
                    <button
                        key={index}
                        onClick={() => setSelectedSize(size)}
                        className={`px-4 py-2 border rounded-lg ${
                            size === selectedSize
                                ? 'border-orange-500 bg-orange-50'
                                : 'border-gray-200 hover:border-gray-300'
                        }`}
                    >
                      {size}
                    </button>
                ))}
              </div>
            </div>

            <button
                onClick={() => addToCart(productData._id, selectedSize)}
                disabled={!selectedSize}
                className={`px-6 py-3 text-white rounded-lg ${
                    selectedSize ? 'bg-black hover:bg-gray-800' : 'bg-gray-400 cursor-not-allowed'
                }`}
            >
              ADD TO CART
            </button>

            <div className='pt-4 space-y-2 text-sm text-gray-500'>
              <p>✔️ 100% Original product</p>
              <p>✔️ Cash on delivery available</p>
              <p>✔️ Easy return and exchange within 7 days</p>
            </div>
          </div>
        </div>

        {/* Product Description */}
        <div className='mt-12 border-t pt-8'>
          <div className='mb-4'>
            <h2 className='text-xl font-semibold'>Product Details</h2>
          </div>
          <p className='text-gray-600'>{productData.description}</p>
        </div>

        {/* Related Products */}
        {productData.category && (
            <RelatedProducts
                category={productData.category}
                currentProductId={productData._id}
            />
        )}
      </div>
  );
};

export default Product;
