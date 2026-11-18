import HeroSection from "../components/landing/HeroSection.jsx";
import FeaturesSection from "../components/landing/FeaturesSection.jsx";
import CTASection from "../components/landing/CTASection.jsx";
import {features} from "../assets/data.js";

const Landing = () => {

    return(
        <div className="overflow-y-auto h-screen bg-gradient-to-b from-gray-50 to-gray-100">
            {/* Hero Section */}
            <HeroSection />

            {/* Features Section */}
            <FeaturesSection features={features}/>

            {/* CTA Section */}
            <CTASection/>

        </div>
    )
}

export default Landing;