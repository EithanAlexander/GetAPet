import React, { useEffect, useState } from 'react';
import { Link, Outlet, useNavigate } from 'react-router-dom';

/**
 * Ads component that serves as the main page for displaying and managing ads.
 * On the initial load, it navigates to the 'seekingAHome' route.
 * Contains navigation links for 'Seeking a home' and 'Create New Ad'.
 */
function Ads() {
  const navigate = useNavigate();
  const [initialLoad, setInitialLoad] = useState(true);

  /**
   * useEffect hook to handle the initial navigation to the 'seekingAHome' route.
   * This runs only on the first render due to the dependency on 'initialLoad'.
   */
  useEffect(() => {
    if (initialLoad) {
      navigate('seekingAHome');
      setInitialLoad(false);
    }
  }, [navigate, initialLoad]);

  return (
    <div>
      <h2>Ads Page</h2>
      <nav>
        <ul>
          <li><Link to="seekingAHome">Seeking a home</Link></li>
          <li><Link to="newAd"><button className="button" type="button">Create New Ad</button></Link></li>
        </ul>
      </nav>
      <Outlet />
    </div>
  );
}

export default Ads;
