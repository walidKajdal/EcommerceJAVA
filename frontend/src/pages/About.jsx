import React from 'react'
import { assets } from '../assets/frontend_assets/assets'
import Title from '../components/Title'
import NewsletterBox from '../components/NewsletterBox'


const About = () => {
  return (
    <div >
      <div className='text-2xl text-center pt-8 border-t'>
        <Title text1={'ABOUT'} text2={'US'} />      
      </div>
      <div className='my-10 flex flex-col md:flex-row gap-16'>
        <img className='w-full md:max-w-[450px] ' src={assets.about_img} alt="" />
        <div className='flex flex-col justify-center gap-6 md:w-2/4 text-gray-600 '>
          <p>Our story begins with a passion for sustainable and accessible fashion. Founded in 2025, our boutique has made it our mission to offer clothing and accessories that combine style, quality, and ecological consciousness.</p>
          <p>Each piece we select is carefully chosen, prioritizing responsible materials and manufacturers who share our values of ethics and transparency. Our collection combines timeless basics and trendy pieces that easily integrate into your existing wardrobe.</p>
          <b className='text-gray-800'>Our Mission </b>
          <p>Our mission is simple: to help you express your personal style while reducing the environmental impact of fashion. We believe it's possible to dress well without compromising your values, and we are committed to offering you choices that make you feel as good on the inside as you look on the outside.</p>
            <p>By choosing our boutique, you support a movement toward a more responsible fashion industry and invest in clothing designed to last, not just for a season.</p>
        </div>
      </div>

      <div className='text-xl py-4 '>
        <Title text1={'WHY'} text2={'CHOOSE US'} />
      </div>

      <div className='flex flex-col md:flex-row text-sm mb-20'>
        <div className='border px-10 md:px-16 py-8 sm:py-20 flex flex-col gap-5'>
          <b>Quality Assurance:</b>
          <p className='text-gray-600'>We take pride in offering only the highest quality products. Each item in our collection is carefully selected and tested to ensure durability and comfort. We work directly with responsible manufacturers who share our commitment to excellence and ethical practices, ensuring you receive clothing that maintains its beauty and shape over time.
          </p>
        </div>
        <div className='border px-10 md:px-16 py-8 sm:py-20 flex flex-col gap-5'>
          <b>Convenience:</b>
          <p className='text-gray-600'>Shopping with us is simple and enjoyable. Our intuitive website allows you to easily navigate our collections, with custom filters to find exactly what you're looking for. We offer fast shipping, hassle-free returns, and a secure payment system. Whether you shop online or in our store, the experience is always smooth and stress-free.
          </p>
        </div>
        <div className='border px-10 md:px-16 py-8 sm:py-20 flex flex-col gap-5'>
          <b>Exceptional Customer Service :</b>
          <p className='text-gray-600'>Our dedicated team is there for you every step of the way. From personalized advice on sizes and styles to questions about orders, we're always ready to help. We believe exceptional customer service means listening to your needs and exceeding your expectations. Your satisfaction is our absolute priority, and we are committed to creating a positive and memorable shopping experience</p>
        </div>
      </div>

      <NewsletterBox/>


    </div>
  )
}

export default About
