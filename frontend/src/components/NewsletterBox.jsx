import React from 'react'

const NewsletterBox = () => {

    const onSubmitHandler = (e) => { 
        event.preventDefault();
    }
  return (
    <div className='text-center'>
        <p className='text-2xl font-medium text-gray-800'>
            Subscribe now & get 20% off
        </p>
        <p className='text-gray-400 mt-3'>
            Sign up today and enjoy a special 20% discount on your first purchase. Our products are designed to bring convenience, style, and quality into your life. With every order, you’re not just buying a product, you’re making an investment in the best.

        </p>
        <p className='text-gray-400 mt-3'>
            Don’t miss out! Get access to exclusive offers, new arrivals, and exciting updates by entering your email below.

        </p>
        <p className='text-gray-400 mt-3'>
            Enter your email to subscribe and start saving today!
        </p>
        <form  onSubmit = {onSubmitHandler} className='w-full sm:w-1/2 flex items-center gap-3 mx-auto my-6 border pl-3'>
            <input className='w-full sm:flex-1 outline-none' type="email" placeholder='Enter your email' required />
            <button type='submit' className='bg-black text-white tex-xs px-10 py-4'>SUBSCRIBE</button>
        </form>
      
    </div>
  )
}

export default NewsletterBox
