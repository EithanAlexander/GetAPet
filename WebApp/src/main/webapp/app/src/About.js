import logo from './media/logo.png';
import { Link } from 'react-router-dom';

/**
 * About us component that provides some details on the app creators.
 */
function About() {
  return (
    <div>
      <h2>About Us</h2>
      <p>Nice to meet you, we are Almog and Eithan 😊</p>
      <p>We are students in the last year of our computer science B.Sc,</p>
      <p>and proud parents to a lovely and fat cat named Aslan.</p>
      <p>This is our final project for the Java workshop course.</p>

      <p style={{ marginTop: '40px' }}>We hope you'll enjoy visiting our web app.</p>
      <p>If you have any questions, feel free to <a href="mailto:filavmooki@gmail.com">contact us</a>.</p>
      <Link to="/ads/seekingAHome">
        <img src={logo} alt="Logo" style={{ width: '200px', marginRight: '5px' }} />
      </Link>

    </div>
  );
}

export default About;
