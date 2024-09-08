import React, { useCallback, useState } from 'react';
import { BASE_URL, CATEGORIES, ADS_PER_PAGE } from './config/config';
import Ad from './Ad';
import usePagination from './hooks/usePagination';

/**
 * SeekingHome component displays ads and provides pagination, filtering, and marking as favorites functionalities.
 *
 * @param {Object} props - The component props. The object being passed from parent component.
 * @param {Object} props.user - The user property inside the props object, contains user details.
 * @param {boolean} props.isAuthenticated - The authentication status of the user, saved as a property in object props.
 */
function SeekingHome({ user, isAuthenticated }) {
  const [category, setCategory] = useState('');

  /**
   * Fetches ads from the server based on the current page number, ads per page, and selected category.
   *
   * @param {number} pageNum - The current page number.
   * @param {number} adsPerPage - The number of ads per page.
   * @returns {Object} The fetched ads and total ads count.
   */
  const fetchAds = useCallback(async (pageNum, adsPerPage) => {
    const response = await fetch(`${BASE_URL}/get_all_ads?pageNum=${pageNum}&adsPerPage=${adsPerPage}&category=${category}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    if (!response.ok) throw new Error('Failed to fetch ads');
    const data = await response.json();
    return { data: data.ads, totalAds: data.totalAds };
  }, [category]);

  const {
    data: ads,
    setData: setAds,
    loading,
    error,
    setError,
    currentPage,
    setCurrentPage,
    adsPerPage,
    totalAds,
    handleNextPage,
    handlePreviousPage,
    handleAdsPerPageChange,
  } = usePagination(fetchAds);

  /**
   * Handles the category selection change upon filtering ads.
   *
   * @param {Object} event - The event object from the category selection change.
   */
  const handleCategoryChange = (event) => {
    setCategory(event.target.value);
    setCurrentPage(1);
  };

  /**
   * Adds an ad to the user's favorites.
   *
   * @param {number} adId - The ID of the selected ad.
   */
  const handleFavorite = async (adId) => {
    if (!isAuthenticated) {
      alert('You must be logged in to favorite an ad');
      return;
    }

    console.log(`Favouring ad with ID: ${adId}`); // Add logging to debug

    try {
      const response = await fetch(`${BASE_URL}/add_ads_to_favorites?user_id=${user.id}&ad_id=${adId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        const errorMsg = await response.text();
        alert(errorMsg);
        throw new Error('Failed to favorite ad');
      }

      alert('Ad was added to your favorites');
    } catch (error) {
      console.error('Failed to add this ad to favorites:', error);
    }
  };

  return (
    <div>
      <h2>Seeking Home</h2>
      {loading ? (
        <p>Loading ads...</p>
      ) : error ? (
        <p>Error: {error}</p>
      ) : (
        <div>
          <div>
            <label htmlFor="adsPerPage">Ads per page: </label>
            <select id="adsPerPage" value={adsPerPage} onChange={handleAdsPerPageChange}>
              {ADS_PER_PAGE.map(num => (
                <option key={num} value={num}>{num}</option>
              ))}
            </select>
          </div>
          <div>
            <label htmlFor="category">Category: </label>
            <select id="category" value={category} onChange={handleCategoryChange}>
              <option value="">All</option>
              {CATEGORIES.map((cat) => (
                <option key={cat} value={cat}>{cat}</option>
              ))}
            </select>
          </div>
          <p><strong>All ads</strong></p>
          {ads.map((ad) => (
            <Ad key={ad.ad_id} ad={ad} handleFavorite={handleFavorite} user={user} />
          ))}
          <div>
            <button onClick={handlePreviousPage} disabled={currentPage === 1}>Previous</button>
            <span> Page {currentPage} of {Math.ceil(totalAds / adsPerPage) || 1} </span>
            <button onClick={handleNextPage} disabled={currentPage >= Math.ceil(totalAds / adsPerPage)}>Next</button>
          </div>
        </div>
      )}
    </div>
  );
}

export default SeekingHome;
